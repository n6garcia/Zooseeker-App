package com.example.zooseeker_cse_110_team_30;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jgrapht.alg.util.Triple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The directions activity for this application.
 * @see "https://developer.android.com/reference/android/app/Activity.html"
 */
public class DirectionsActivity extends AppCompatActivity {
    private TextView exhibitName; //large name of exhibit
    private TextView directionsText; //directions through park
    private TextView nextText; //next exhibit name + distance
    private Button nextButton; //next button
    private Button backButton; //back button

    public ExhibitViewModel viewModel; //manages UI data + handlers
    //Triple: {exhibit name, full directions, distance to exhibit}
    private List<Triple<String, String, Integer>> visitPlan; //ordered List of exhibits to visit
    private int currentExhibit; //index of current exhibit in visit list

    /**
     * Function that runs when this Activity is created. Set up most classes.
     * @param savedInstanceState Most recent Bundle data, otherwise null
     * @see "https://developer.android.com/reference/android/app/Activity#onCreate(android.os.Bundle)"
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        viewModel = new ViewModelProvider(this)
                .get(ExhibitViewModel.class); //get ExhibitViewModel from the provider

        //set up instance var textViews
        this.exhibitName = this.findViewById(R.id.exhibit_name);
        this.directionsText = this.findViewById(R.id.directions_text);
        this.nextText = this.findViewById(R.id.next_text);

        // Set exhibit name text color to black
        //exhibitName.setTextColor(getResources().getColor(R.color.black));

        // set up next button click
        this.nextButton = this.findViewById(R.id.next_btn); //get search button from layout
        nextButton.setOnClickListener(this::onNextButtonClicked);

        // set up back button click
        this.backButton = this.findViewById(R.id.back_btn); //get search button from layout
        backButton.setOnClickListener(this::onBackButtonClicked);

        //do visit exhibit processing
        processVisitList();
        currentExhibit = 0;
        updateText();
    }

    /**
     * Event handler for clicking the next button.
     * @param view The View which contains the search button and search bar.
     */
    public void onNextButtonClicked(View view){
        updateText();
    }

    /**
     * Event handler for clicking the back button.
     * @param view The View which contains the search button and search bar.
     */
    public void onBackButtonClicked(View view){
        currentExhibit -= 2; //because it has already been incremented
        if(currentExhibit < 0) { //clicked back button on first exhibit
            currentExhibit = 0; //it crashes if we don't do this (thread issue maybe?)
            finish(); //exit activity
        }
        updateText();
    }

    /**
     * Utility function for updating UI.
     */
    private void updateText() {
        if(currentExhibit < visitPlan.size()) { //not reached end of visit plan
            //triple corresponding to the current exhibit {name, directions, distance}
            Triple<String, String, Integer> currTriple = visitPlan.get(currentExhibit);
            exhibitName.setText(currTriple.getFirst()); //name
            directionsText.setText(currTriple.getSecond()); //directions

            if(currentExhibit < visitPlan.size() - 1) { //display next if not on last exhibit
                //get triple of next exhibit
                Triple<String, String, Integer> nextTriple = visitPlan.get(currentExhibit + 1);
                nextText.setText("Next: " + nextTriple.getFirst() + ", "
                        + nextTriple.getThird() + " ft"); //display next name + distance
            }
            else {
                nextText.setText(""); //no next exhibit to display (on last exhibit of plan)
            }
            currentExhibit++; //increment current exhibit (to go back, decrement by 2)
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
        List<Exhibit> unorderedVisitList = this.viewModel.getSelectedExhibits();
        Directions dir = new Directions(this.getApplication().getApplicationContext());

        //list of edge paths, list of ordered exhibit identities, initialize visitPlan
        List<List<IdentifiedWeightedEdge>> edgeList = dir.findShortestRoute(unorderedVisitList);
        List<String> orderedVisitList = dir.getOrderedExhibitList();
        this.visitPlan = new ArrayList<>();

        String name; //formatted name string
        String path; //directions string
        int nextDist; //distance to next exhibit
        Triple<String, String, Integer> exhibitTriple; //collection of data for each exhibit
        List<String> roadList = new ArrayList<>(); //list of roads for direction processing

        //iterate through the exhibits in the order in which they are visited
        for(int i = 0; i < orderedVisitList.size() - 1; i++) {
            List<IdentifiedWeightedEdge> nextPath = edgeList.get(i); //path away from curr vertex

            //get the name of the next exhibit (the one we are navigating to)
            name = viewModel.getExhibitIdentity(orderedVisitList.get(i + 1)).name;
            nextDist = dir.calculatePathWeight(nextPath); //distance to next exhibit

            //find the names of all the roads in the path to the next exhibit
            roadList.clear(); //reset roadList
            for(IdentifiedWeightedEdge edge : nextPath) { //iterate through edges in path
                String street = dir.getEdgeInfo().get(edge.getId()).street; //get road name
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
}