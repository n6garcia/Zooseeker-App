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
        updateText();
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
        targetExhibit = Directions.getClosestUnvisitedExhibit(targetExhibit);
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

    /**
     * Utility function for updating UI.
     */
    private void updateText() { //TODO remove
        if(currentExhibitIndex < visitPlan.size()) { //not reached end of visit plan
            //triple corresponding to the current exhibit {name, directions, distance}
            Triple<String, String, Integer> currTriple = visitPlan.get(currentExhibitIndex);
            exhibitName.setText(currTriple.getFirst()); //name
            directionsText.setText(currTriple.getSecond()); //directions

            if(currentExhibitIndex < visitPlan.size() - 1) { //display next if not on last exhibit
                //get triple of next exhibit
                Triple<String, String, Integer> nextTriple = visitPlan.get(currentExhibitIndex + 1);
                nextText.setText("Next: " + nextTriple.getFirst() + ", "
                        + nextTriple.getThird() + " ft"); //display next name + distance
            }
            else {
                nextText.setText(""); //no next exhibit to display (on last exhibit of plan)
            }
            currentExhibitIndex++; //increment current exhibit (to go back, decrement by 2)
        }
        else { //reached end of plan and attempted to click next - show no exhibits left alert
            Utilities.showAlert(this, "You've reached the end of the plan.");
        }
    }

    /**
     * Utility method - processes the List passed in from MainActivity to get UI elements.
     * Note: only call after viewModel has been initialized.
     */
    private void processVisitList() { //TODO remove
        //get list of exhibits to visit and Directions object
        List<Exhibit> visitList = Directions.findVisitPlan();
        this.visitPlan = new ArrayList<>();

        String name; //formatted name string
        String path; //directions string
        int nextDist; //distance to next exhibit
        Triple<String, String, Integer> exhibitTriple; //collection of data for each exhibit
        List<String> roadList = new ArrayList<>(); //list of roads for direction processing

        //iterate through the exhibits in the order in which they are visited
        for(int i = 0; i < visitList.size() - 1; i++) {
            List<IdentifiedWeightedEdge> nextPath = Directions
                    .findShortestPath(visitList.get(i), visitList.get(i + 1)); //path away from curr vertex

            //get the name of the next exhibit (the one we are navigating to)
            name = visitList.get(i + 1).name;
            nextDist = Directions.calculatePathWeight(nextPath); //distance to next exhibit

            //find the names of all the roads in the path to the next exhibit
            roadList.clear(); //reset roadList
            for(IdentifiedWeightedEdge edge : nextPath) { //iterate through edges in path
                String street = Directions.getEdgeInfo().get(edge.getId()).street; //get road name
                if(!roadList.contains(street)) { //new road name encountered
                    roadList.add(street); //add new road name to list of roads
                }
            }
            //roadList now contains the list of unique road names in the path, in order
            path = ""; //default empty string
            if(roadList.size() > 0) { //there is a road on the path
                path = "Proceed down " + roadList.get(0); //direction for first road
                //add rest of roads (loop only runs when there are more than 1 roads)
                for(int j = 1; j < roadList.size(); j++) {
                    path = path + "\nThen down " + roadList.get(j); //each road on new line
                }
                path = path + "\n\nArrive in " + nextDist + " ft"; //add distance to exhibit
            }

            exhibitTriple = new Triple<>(name, path, nextDist); //create data collection
            visitPlan.add(exhibitTriple); //add to plan
        }
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
        Exhibit currentExhibit = userCurrentExhibit;
        for(int edgeNum = 0; edgeNum < path.size(); edgeNum++) {
            IdentifiedWeightedEdge currEdge = path.get(edgeNum);
            String nextNode = getNextNode(currEdge, currentExhibit); //next vertex
            String streetName = Directions.getEdgeInfo().get(currEdge.getId()).street; //name of edge
            int distance = (int) Directions.getGraph().getEdgeWeight(currEdge); //edge "length" //TODO update if they give us double distances

            //add "Proceed on" / "Continue on"
            if(!streetName.equals(lastStreetName)) { //new street encountered
                directions = directions + "Proceed on "; //new street convention
                lastStreetName = streetName; //update last street encountered
            }
            else { //on the same street as last edge
                directions = directions + "Continue on "; //no need to update last street name
            }

            //add distance
            directions = directions + distance + " ft ";

            //add "towards" / "to"
            for(int j = 1; j < roadList.size(); j++) {
                detPath = detPath + "\nThen down " + roadList.get(j); //each road on new line
                detPath = detPath + " " + weightList.get(j) + " ft";

                exhibitName = this.viewModel.getExhibit(nodeList.get(j)).name;

                if (j == roadList.size()-1){
                    detPath = detPath + " to the " + exhibitName;
                } else {
                    detPath = detPath + " towards " + exhibitName;
                }
            }
            detPath = detPath + "\n\nArriving in " + nextDist + " ft"; //add distance to exhibit
        }
    }

    private String getBriefDirections() {
        return ""; //TODO
    }

    /**
     * Utility method that returns the node at the end of the edge away from a given node.
     * @param edge The edge to be analyzed.
     * @param node The source node.
     * @return Whichever of the edge's source/target is NOT the input node.
     */
    private String getNextNode(IdentifiedWeightedEdge edge, Exhibit node) {
        String edgeSource = Directions.getGraph().getEdgeSource(edge); //potential other node
        if(node.equals(edgeSource)) { //node is the same as potential node, return other end of edge
            return Directions.getGraph().getEdgeTarget(edge);
        }
        return edgeSource; //node not same as potential node, return this end of edge
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