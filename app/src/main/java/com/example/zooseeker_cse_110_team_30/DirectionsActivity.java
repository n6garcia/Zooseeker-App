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

public class DirectionsActivity extends AppCompatActivity {
    private TextView exhibitName;
    private TextView directionsText;
    private TextView nextText;
    private Button nextButton;
    private Button backButton; //back button

    public ExhibitViewModel viewModel; //manages UI data + handlers
    private List<List<IdentifiedWeightedEdge>> edgeList; //list of edges in the visit plan, ordered.
    private List<Triple<String, String, Integer>> visitPlan; //ordered List of exhibits to visit
    private int currentExhibit; //index of current exhibit in Lists

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        viewModel = new ViewModelProvider(this)
                .get(ExhibitViewModel.class); //get ExhibitViewModel from the provider

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

        processVisitList();
        currentExhibit = 0;
        updateText();
    }

    public void onNextButtonClicked(View view){
        updateText();
    }

    public void onBackButtonClicked(View view){
        currentExhibit -= 2;
        if(currentExhibit < 0) {
            currentExhibit = 0;
            finish();
        }
        updateText();
    }

    private void updateText() {
        if(currentExhibit < visitPlan.size()) {
            Triple<String, String, Integer> currTriple = visitPlan.get(currentExhibit);
            exhibitName.setText(currTriple.getFirst());
            directionsText.setText(currTriple.getSecond());
            if(currentExhibit < visitPlan.size() - 1) {
                Triple<String, String, Integer> nextTriple = visitPlan.get(currentExhibit + 1);
                nextText.setText("Next: " + nextTriple.getFirst() + ", "
                        + nextTriple.getThird() + " ft");
            }
            else {
                nextText.setText("");
            }
            currentExhibit++;
        }
        else {
            Utilities.showAlert(this, "You've reached the end of the plan.");
        }
    }

    /**
     * Utility method - processes the List passed in from MainActivity to get UI elements.
     * Note: only call after viewModel has been initialized.
     */
    private void processVisitList() {
        List<Exhibit> unorderedVisitList = this.viewModel.getSelectedExhibits();
        Directions dir = new Directions(this.getApplication().getApplicationContext());
        this.edgeList = dir.findShortestRoute(unorderedVisitList);
        List<String> orderedVisitList = dir.getOrderedExhibitList();
        this.visitPlan = new ArrayList<>();

        //first exhibit set
        Exhibit exhibit;
        String name;
        String path;
        int nextDist;
        Triple<String, String, Integer> exhibitTriple;
        List<String> roadList = new ArrayList<>();

        for(int i = 0; i < orderedVisitList.size() - 1; i++) {
            List<IdentifiedWeightedEdge> nextPath = edgeList.get(i); //path away from curr vertex

            exhibit = viewModel.getExhibitIdentity(orderedVisitList.get(i + 1)); //next exhibit
            name = exhibit.name;
            nextDist = dir.calculatePathWeight(nextPath); //next total distance

            roadList.clear();
            for(IdentifiedWeightedEdge edge : nextPath) {
                String street = dir.getEdgeInfo().get(edge.getId()).street;
                if(!roadList.contains(street)) {
                    roadList.add(street);
                }
            }
            path = ""; //clear string
            if(roadList.size() > 0) {
                path = "Proceed down " + roadList.get(0);
                if(roadList.size() > 1) {
                    for(int j = 1; j < roadList.size(); j++) {
                        path = path + "\nThen down " + roadList.get(j);
                    }
                }
                path = path + "\n\nArrive in " + nextDist + " ft";
            }

            exhibitTriple = new Triple<>(name, path, nextDist);
            visitPlan.add(exhibitTriple);
        }
    }
}