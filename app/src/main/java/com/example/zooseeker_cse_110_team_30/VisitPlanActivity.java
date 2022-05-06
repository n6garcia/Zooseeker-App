package com.example.zooseeker_cse_110_team_30;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

/**
 * The visit plan activity for this application.
 * @see "https://developer.android.com/reference/android/app/Activity.html"
 */
public class VisitPlanActivity extends AppCompatActivity {
    public RecyclerView recyclerView; //for plan display
    public ExhibitViewModel viewModel; //manages UI data + handlers
    private Button directionsButton; //directions button for next Activity
    private VisitExhibitAdapter adapter; //adapts DAO/lists of exhibits to UI

    private List<IdentifiedWeightedEdge> edgeList; //list of edges in the visit plan, ordered.

    /**
     * Function that runs when this Activity is created. Set up most classes.
     * @param savedInstanceState Most recent Bundle data, otherwise null
     * @see "https://developer.android.com/reference/android/app/Activity#onCreate(android.os.Bundle)"
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_plan); //update which layout is displaying

        Bundle extras = getIntent().getExtras();
        this.edgeList = (List<IdentifiedWeightedEdge>) extras.getSerializable("visit_list");

        viewModel = new ViewModelProvider(this)
                .get(ExhibitViewModel.class); //get ExhibitViewModel from the provider

        //create ExhibitAdapter and set it up
        adapter = new VisitExhibitAdapter(); //create adapter
        adapter.setHasStableIds(true);
        //get and start observing LiveData Exhibits. When change detected, call setExhibits.

        //get RecyclerView from layout and set it up
        this.recyclerView = findViewById(R.id.visit_exhibits);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //set up directions button click listener/handler
        this.directionsButton = this.findViewById(R.id.directions_button); //get button from layout
        directionsButton.setOnClickListener(this::onDirectionsButtonClicked);

        adapter.setExhibits(viewModel.getSelectedExhibits());
    }

    /**
     * Event handler for clicking the directions button button.
     * @param view The View which contains the directions button.
     */
    public void onDirectionsButtonClicked(View view) {
        //Intent directionsIntent = new Intent(this, DirectionsActivity.class);
        //directionsIntent.putExtra("visit_list", this.edgeList);
        //startActivity(directionsIntent);
    }
}