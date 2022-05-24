package com.example.zooseeker_cse_110_team_30;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    //TODO remove
    //public ExhibitViewModel viewModel; //manages UI data + handlers
    //Triple: {exhibit name, full directions, distance to exhibit}
    private List<Triple<String, String, Integer>> visitPlan; //DEPRECATED //TODO remove

    private String briefDirections; //supercedes visitPlan
    private String detailedDirections; //supercedes visitPlan
    private int currentExhibitIndex; //index of current exhibit in visit list

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

        // set up skip button click
        this.previousButton = this.findViewById(R.id.previous_button); //get search button from layout
        previousButton.setOnClickListener(this::onPreviousButtonClicked);

        // set up skip button click
        this.skipButton = this.findViewById(R.id.skip_button); //get search button from layout
        skipButton.setOnClickListener(this::onSkipButtonClicked);

        // set up next button click
        this.nextButton = this.findViewById(R.id.next_button); //get search button from layout
        nextButton.setOnClickListener(this::onNextButtonClicked);

        //do visit exhibit processing
        processVisitList();
        this.currentExhibitIndex = 0;
        updateText();

        PermissionChecker permissionChecker = new PermissionChecker(this);
        if (permissionChecker.ensurePermissions()) {
            return;
        }

        String provider = LocationManager.GPS_PROVIDER;
        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                locationChangedHandler(location);
            }
        };
        try {
            locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
        }
        catch(SecurityException e) {
            return;
        }

        this.dao = ExhibitDatabase.getSingleton(this.getApplicationContext()).exhibitDao();
        this.targetExhibit = dao.get("entrance_exit_gate"); //TODO immediately call next exhibit logic
        this.userCurrentExhibit = dao.get("entrance_exit_gate");
        this.replanPrompted = false;
    }

    /**
     * Event handler for clicking the back button.
     * @param view The View which contains the search button and search bar.
     */
    public void onPreviousButtonClicked(View view){
        this.currentExhibitIndex -= 2; //because it has already been incremented
        if(currentExhibitIndex < 0) { //clicked back button on first exhibit
            currentExhibitIndex = 0; //it crashes if we don't do this (thread issue maybe?)
            finish(); //exit activity
        }
        updateText();
    }

    /**
     *
     * @param view
     */
    public void onSkipButtonClicked(View view){
        //TODO unselect this exhibit and replan
    }

    /**
     * Event handler for clicking the next button.
     * @param view The View which contains the search button and search bar.
     */
    public void onNextButtonClicked(View view){
        updateText();
    }

    /**
     * Utility function for updating UI.
     */
    private void updateText() {
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
    private void processVisitList() {
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

    public void locationChangedHandler(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Exhibit closestExhibit = Directions.getClosestAbsoluteExhibit(lat, lng);
        if(closestExhibit != userCurrentExhibit) { //use Exhibit.equals()
            updateCurrentExhibit(closestExhibit); //update user current exhibit
        }
    }

    private void updateCurrentExhibit(Exhibit exhibit) {
        userCurrentExhibit = exhibit;

        Exhibit closestUnvisitedExhibit = Directions.getClosestUnvisitedExhibit(userCurrentExhibit);
        if(closestUnvisitedExhibit != targetExhibit) {
            //user is off track - closer to another unvisited exhibit
            if(replanPrompted == false) { //user has not yet been prompted for a replan
                boolean replanAccepted = promptReplan();
                if(replanAccepted) {
                    targetExhibit = closestUnvisitedExhibit;
                }
                replanPrompted = true; //don't replan a second time
            }
        }

        updateDirections(); //update directions to next exhibit
    }
}