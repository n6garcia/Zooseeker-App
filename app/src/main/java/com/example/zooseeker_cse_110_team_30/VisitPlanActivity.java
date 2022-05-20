package com.example.zooseeker_cse_110_team_30;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.jgrapht.alg.util.Triple;

import java.util.ArrayList;
import java.util.List;

/**
 * The visit plan activity for this application.
 * @see "https://developer.android.com/reference/android/app/Activity.html"
 */
public class VisitPlanActivity extends AppCompatActivity {
    public RecyclerView recyclerView; //for plan display
    public ExhibitViewModel viewModel; //manages UI data + handlers
    private Button directionsButton; //directions button for next Activity
    private Button backButton; //back button
    private VisitExhibitAdapter adapter; //adapts DAO/lists of exhibits to UI

    //Exhibit triple: {Exhibit object, exhibit street name, total distance}
    private List<Triple<Exhibit, String, Integer>> visitPlan; //ordered List of exhibits to visit

    /**
     * Function that runs when this Activity is created. Set up most classes.
     * @param savedInstanceState Most recent Bundle data, otherwise null
     * @see "https://developer.android.com/reference/android/app/Activity#onCreate(android.os.Bundle)"
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_plan); //update which layout is displaying

        viewModel = new ViewModelProvider(this)
                .get(ExhibitViewModel.class); //get ExhibitViewModel from the provider

        //create ExhibitAdapter and set it up
        adapter = new VisitExhibitAdapter(); //create adapter
        adapter.setHasStableIds(true);

        //get RecyclerView from layout and set it up
        this.recyclerView = findViewById(R.id.visit_exhibits);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //set up directions button click listener/handler
        this.directionsButton = this.findViewById(R.id.directions_button); //get button from layout
        directionsButton.setOnClickListener(this::onDirectionsButtonClicked);

        //set up back button click listener/handler
        this.backButton = this.findViewById(R.id.back_button); //get button from layout
        backButton.setOnClickListener(this::onBackButtonClicked);

        processVisitList(); //process visit plan data
        adapter.setExhibits(this.visitPlan); //display visit plan
    }

    /**
     * Event handler for clicking the directions button.
     * @param view The View which contains the directions button.
     */
    public void onDirectionsButtonClicked(View view) {
        Intent directionsIntent = new Intent(this, DirectionsActivity.class);
        startActivity(directionsIntent); //start a DirectionsActivity
    }

    /**
     * Event handler for clicking the back button.
     * @param view The View which contains the back button.
     */
    public void onBackButtonClicked(View view) {
        finish(); //close this activity
    }

    /**
     * Utility method - processes the List passed in from MainActivity to get UI elements.
     * Note: only call after viewModel has been initialized.
     */
    private void processVisitList() {
        //get list of exhibits to visit and Directions object
        List<Exhibit> unorderedVisitList = this.viewModel.getSelectedExhibits();

        //list of edge paths, list of ordered exhibit identities, initialize visitPlan
        List<List<IdentifiedWeightedEdge>> edgeList = dir.findShortestRoute(unorderedVisitList); //TODO fix static
        List<String> orderedVisitList = dir.getOrderedExhibitList(); //TODO fix static
        this.visitPlan = new ArrayList<>();

        //first exhibit - always entrance gate, "Entrance Way", 0 distance
        Exhibit exhibit = viewModel.getExhibitIdentity("entrance_exit_gate");
        String loc = "Entrance Way";
        int totalDist = 0;
        //create Exhibit data triple and add to the plan
        Triple<Exhibit, String, Integer> exhibitTriple = new Triple<>(exhibit, loc, totalDist);
        visitPlan.add(exhibitTriple);

        //iterate through list of exhibts in order of visit
        for(int i = 0; i < orderedVisitList.size() - 1; i++) {
            List<IdentifiedWeightedEdge> nextPath = edgeList.get(i); //path away from curr vertex
            IdentifiedWeightedEdge lastEdge = nextPath.get(nextPath.size() - 1); //just before next

            exhibit = viewModel.getExhibitIdentity(orderedVisitList.get(i + 1)); //next exhibit
            //we make the location of the exhibit the name of the last road on the way there
            loc = dir.getEdgeInfo().get(lastEdge.getId()).street; //next location
            totalDist += dir.calculatePathWeight(nextPath); //increment total distance

            //create data triple and add it to the visit
            exhibitTriple = new Triple<>(exhibit, loc, totalDist);
            visitPlan.add(exhibitTriple);
        }
    }
}