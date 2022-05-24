package com.example.zooseeker_cse_110_team_30;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import org.jgrapht.alg.util.Triple;

import java.util.ArrayList;
import java.util.List;

/**
 * The directions activity for this application.
 * @see "https://developer.android.com/reference/android/app/Activity.html"
 */
public class DirectionsActivity extends AppCompatActivity {
    private TextView exhibitName; //large name of exhibit
    private TextView directionsText; //directions through park
    private TextView nextText; //next exhibit name + distance
    private Button previousButton; //back button
    private Button skipButton; //skip button
    private Button nextButton; //next button
    private Switch detailedSwitch; //detailed directions switch

    //TODO remove
    //public ExhibitViewModel viewModel; //manages UI data + handlers
    //Triple: {exhibit name, full directions, distance to exhibit}
    private List<Triple<String, String, Integer>> visitPlan; //DEPRECATED //TODO remove

    private List<Exhibit> visitHistory;
    private boolean detailedDirections;
    private int currentExhibitIndex; //index of current exhibit in visit list //TODO replace with visitHistory size

    private static ExhibitDao dao; //exhibit database
    private Exhibit targetExhibit; //exhibit user is navigating to
    private Exhibit userCurrentExhibit; //exhibit user is closest to
    private boolean replanPrompted; //whether or not a replan has been prompted for this exhibit

    /**
     * Function that runs when this Activity is created. Set up most classes.
     * @param savedInstanceState Most recent Bundle data, otherwise null
     * @see "https://developer.android.com/reference/android/app/Activity#onCreate(android.os.Bundle)"
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        //TODO remove?
        //viewModel = new ViewModelProvider(this)
        //        .get(ExhibitViewModel.class); //get ExhibitViewModel from the provider

        //set up instance var textViews
        this.exhibitName = this.findViewById(R.id.exhibit_name);
        this.directionsText = this.findViewById(R.id.directions_text);
        this.nextText = this.findViewById(R.id.next_text);

        // set up back button click
        this.previousButton = this.findViewById(R.id.previous_button); //get button from layout
        previousButton.setOnClickListener(this::onPreviousButtonClicked);

        // set up skip button click
        this.skipButton = this.findViewById(R.id.skip_button); //get button from layout
        skipButton.setOnClickListener(this::onSkipButtonClicked);

        // set up next button click
        this.nextButton = this.findViewById(R.id.next_button); //get button from layout
        nextButton.setOnClickListener(this::onNextButtonClicked);

        // set up detailed switch click
        this.detailedSwitch = this.findViewById(R.id.detailed_directions_switch);
        nextButton.setOnClickListener(this::onDirectionsSwitchToggled);

        this.currentExhibitIndex = 0;

        PermissionChecker permissionChecker = new PermissionChecker(this);
        if (permissionChecker.ensurePermissions()) {
            return;
        }

        String provider = LocationManager.GPS_PROVIDER;
        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) { locationChangedHandler(location); }
        };
        try {
            locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
        }
        catch(SecurityException e) {
            return;
        }

        this.dao = ExhibitDatabase.getSingleton(this.getApplicationContext()).exhibitDao();
        this.targetExhibit = dao.get("entrance_exit_gate");
        this.userCurrentExhibit = dao.get("entrance_exit_gate");
        this.replanPrompted = false;
        this.detailedDirections = false;
        this.visitHistory = new ArrayList<>();

        onNextButtonClicked(nextButton.getRootView());
    }

    /**
     * Event handler for clicking the back button.
     * @param view The View which contains the button.
     */
    public void onPreviousButtonClicked(View view){
        //TODO set current target Exhibit.visited
        this.currentExhibitIndex -= 2; //because it has already been incremented
        if(currentExhibitIndex < 0) { //clicked back button on first exhibit
            currentExhibitIndex = 0; //it crashes if we don't do this (thread issue maybe?)
            finish(); //exit activity
        }
        updateAllText();
    }

    /**
     * Event handler for clicking the skip button.
     * @param view The View which contains the button.
     */
    public void onSkipButtonClicked(View view) {
        //TODO unselect this exhibit before replanning
        replan();
    }

    /**
     * Event handler for clicking the next button.
     * @param view The View which contains the button.
     */
    public void onNextButtonClicked(View view) {
        visitHistory.add(targetExhibit);
        targetExhibit.visited = visitHistory.size();

        if(dao.getUnvisited().size() == 0) { //no more exhibits
            if (targetExhibit.identity.equals("entrance_exit_gate")) { //already going to exit
                Utilities.showAlert(this, "You've reached the end of the plan.");
                return;
            }
            else { //no more exhibits but not navigating to exit, go to exit
                targetExhibit = dao.get("entrance_exit_gate");
            }
        }
        else { //still more exhibits to visit
            targetExhibit = Directions.getClosestUnvisitedExhibit(targetExhibit);
        }
        updateAllText();
    }

    /**
     * Event handler for toggling the detailed directions switch.
     * @param view The View which contains the switch.
     */
    public void onDirectionsSwitchToggled(View view) {
        detailedDirections = detailedSwitch.isChecked();
        updateDirections();
    }

    private void updateAllText() {
        exhibitName.setText(targetExhibit.name);

        Exhibit nextExhibit = Directions.getClosestUnvisitedExhibit(targetExhibit);
        List<IdentifiedWeightedEdge> nextPath = Directions.findShortestPath(targetExhibit, nextExhibit);
        int pathLength = Directions.calculatePathWeight(nextPath);
        String nextExhibitText = "Next: " + nextExhibit.name + ", " + pathLength + " ft";
        nextText.setText(nextExhibitText);

        updateDirections();
    }

    private void updateDirections() {
        if(detailedDirections) {
            directionsText.setText(getDetailedDirections());
        }
        else {
            directionsText.setText((getBriefDirections()));
        }
    }

    private String getDetailedDirections() {
        List<IdentifiedWeightedEdge> path = Directions.findShortestPath(userCurrentExhibit, targetExhibit);

        String directions = ""; // default empty string
        String lastStreetName = "IMPOSSIBLE STREET NAME";
        Exhibit currentNode = userCurrentExhibit;
        Exhibit nextNode = targetExhibit;

        for(int edgeNum = 0; edgeNum < path.size(); edgeNum++) {
            IdentifiedWeightedEdge currEdge = path.get(edgeNum);
            nextNode = getNextNode(currEdge, currentNode); //next vertex
            String streetName = Directions.getEdgeInfo().get(currEdge.getId()).street; //name of edge
            int distance = (int) Directions.getGraph().getEdgeWeight(currEdge); //edge "length" //TODO update if they give us double distances

            if(edgeNum > 0) {
                directions = "\n"; //newline if not first direction
            }

            //add "Proceed on" / "Continue on"
            if(!streetName.equals(lastStreetName)) { //new street encountered
                directions = directions + "Proceed on "; //new street convention
                lastStreetName = streetName; //update last street encountered
            }
            else { //on the same street as last edge
                directions = directions + "Continue on "; //no need to update last street name
            }

            directions = directions + distance + " ft "; //add distance

            if(edgeNum == path.size() - 1) { //add "towards" / "to" for last exhibit
                directions = directions + "to ";
            }
            else { //not last exhibit
                directions = directions + "towards ";
            }

            directions = directions + nextNode.name; //add next node name
            currentNode = nextNode;
        }
        if(nextNode.isExhibitGroup()) {
            directions = directions + " and find " + targetExhibit.name + " inside";
        }
        int totalDistance = Directions.calculatePathWeight(path);
        return directions + "\n\nArriving in " + totalDistance + " ft"; //add distance to exhibit
    }

    private String getBriefDirections() {
        List<IdentifiedWeightedEdge> path = Directions.findShortestPath(userCurrentExhibit, targetExhibit);

        String directions = ""; // default empty string
        String lastStreetName = "IMPOSSIBLE STREET NAME";

        for(int edgeNum = 0; edgeNum < path.size(); edgeNum++) {
            IdentifiedWeightedEdge currEdge = path.get(edgeNum);
            String streetName = Directions.getEdgeInfo().get(currEdge.getId()).street; //name of edge

            if(edgeNum <= 0) { //Add "Proceed down" / "Then down" for first exhibit
                directions = "Proceed down " + streetName;
            }
            else if(streetName.equals(lastStreetName)) { //same street as last one
                continue; //don't add new line to directions
            }
            else { //new street encountered
                directions = directions + "Then down " + streetName; //new street convention
                lastStreetName = streetName; //update last street encountered
            }
        }
        if(!targetExhibit.equals(Directions.getParent(targetExhibit))) { //has parent - grouped
            String parentName = Directions.getParent(targetExhibit).name;
            directions = directions + "\nFind " + targetExhibit.name + " inside " + parentName;
        }
        int totalDistance = Directions.calculatePathWeight(path);
        return directions + "\n\nArriving in " + totalDistance + " ft"; //add distance to exhibit
    }

    /**
     * Utility method that returns the node at the end of the edge away from a given node.
     * @param edge The edge to be analyzed.
     * @param node The source node.
     * @return Whichever of the edge's source/target is NOT the input node.
     */
    private Exhibit getNextNode(IdentifiedWeightedEdge edge, Exhibit node) {
        String edgeSource = Directions.getGraph().getEdgeSource(edge); //potential other node
        if(node.equals(edgeSource)) { //node is the same as potential node, return other end of edge
            return dao.get(Directions.getGraph().getEdgeTarget(edge));
        }
        return dao.get(edgeSource); //node not same as potential node, return this end of edge
    }

    public void locationChangedHandler(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Exhibit closestExhibit = Directions.getClosestAbsoluteExhibit(lat, lng);
        if(!closestExhibit.equals(userCurrentExhibit)) { //closest exhibit != current exhibit
            updateCurrentExhibit(closestExhibit); //update user current exhibit
        }
    }

    private void updateCurrentExhibit(Exhibit exhibit) {
        userCurrentExhibit = exhibit;

        Exhibit closestUnvisitedExhibit = Directions.getClosestUnvisitedExhibit(userCurrentExhibit);
        if(closestUnvisitedExhibit != targetExhibit) {
            //user is off track - closer to another unvisited exhibit
            if(!replanPrompted) { //user has not yet been prompted for a replan
                if(promptReplan()) { //true if user accepted replan
                    replan();
                }
                replanPrompted = true; //don't replan a second time
            }
        }

        updateDirections(); //update directions to next exhibit
    }

    /**
     * Displays a message to replan the exhibit with yes and no choices.
     * @return The user's choice. True if they chose to replan, false if not.
     */
    public boolean promptReplan() {
        return false; //TODO implement
    }

    private void replan() {
        targetExhibit = Directions.getClosestUnvisitedExhibit(userCurrentExhibit);
        updateAllText();
    }
}