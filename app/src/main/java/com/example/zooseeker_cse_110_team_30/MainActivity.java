package com.example.zooseeker_cse_110_team_30;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * The main activity for this application.
 * @see "https://developer.android.com/reference/android/app/Activity.html"
 */
public class MainActivity extends AppCompatActivity {
    public RecyclerView recyclerView; //for search results display
    public ExhibitViewModel viewModel; //manages UI data + handlers
    private ImageButton searchButton; //search button for search bar
    private EditText searchBar; //search bar for exhibits
    private Button planButton; //button to go to PlanActivity
    private TextView selectedText; //text
    private ExhibitAdapter adapter; //adapts DAO/lists of exhibits to UI

    /**
     * Function that runs when this Activity is created. Set up most classes.
     * @param savedInstanceState Most recent Bundle data, otherwise null
     * @see "https://developer.android.com/reference/android/app/Activity#onCreate(android.os.Bundle)"
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //update which layout is displaying

        //uncomment this to clear the stored database
        this.getApplicationContext().deleteDatabase("zoo_exhibits.db");
        viewModel = new ViewModelProvider(this)
                .get(ExhibitViewModel.class); //get ExhibitViewModel from the provider
        Directions.setContext(this.getApplicationContext());

        //create ExhibitAdapter and set it up
        adapter = new ExhibitAdapter(); //create adapter
        adapter.setHasStableIds(true);
        adapter.setOnCheckBoxClickedHandler(this::toggleSelected); //exhibit selection handler
        adapter.setExhibits(viewModel.getAllExhibits());
        //adapter.setExhibits(Exhibit.loadJSON(this, "node_info.json"));

        //get RecyclerView from layout and set it up
        this.recyclerView = findViewById(R.id.zoo_exhibits);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        this.searchBar = this.findViewById(R.id.search_bar); //get search bar from layout

        //set up search button click listener/handler
        this.searchButton = this.findViewById(R.id.search_button); //get search button from layout
        searchButton.setOnClickListener(this::onSearchButtonClicked);

        this.planButton = this.findViewById(R.id.plan_button);
        planButton.setOnClickListener(this::onPlanButtonClicked);

        this.selectedText = this.findViewById(R.id.num_selected); //get search bar from layout
        selectedText.setText(viewModel.getSelectedExhibits().size() + " Exhibits Selected");
    }

    /**
     * Event handler for clicking the search button.
     * @param view The View which contains the search button and search bar.
     */
    public void onSearchButtonClicked(View view) {
        String text = this.searchBar.getText().toString(); //get search bar text

        List<Exhibit> searchResults;
        if(text.equals("")) { //if the search box is empty, display all exhibits
            searchResults = viewModel.getAllExhibits();
        }
        else { //search bar contains some text
            //remove commas from query to prevent unexpected substring behavior
            //ex. "r,r" returns Alligators because the tags: "alligator,reptile"
            //problem - try typing out "alligator" with lots of commas everywhere
            //However, shouldn't be an issue for normal user experience, and also cannot crash
            text = text.replace(",", "");
            searchResults = viewModel.query(text); //get search results from DAO
        }
        adapter.setExhibits(searchResults); //update list of displayed exhibits

        /* debug messages
        System.out.println("Search text: \"" + text + "\"");
        System.out.println(searchResults.toString()); */
    }

    /**
     * Event handler for clicking the plan button.
     * @param view The View which contains the plan button.
     */
    public void onPlanButtonClicked(View view) {
        if(viewModel.getSelectedExhibits().size() == 0) {
            Utilities.showAlert(this, "Please select an exhibit.");
        }
        else {
            Intent planIntent = new Intent(this, VisitPlanActivity.class);
            startActivity(planIntent);
        }
    }

    /**
     * Event handler for toggling a checkbox (update selection).
     * @param exhibit The Exhibit which is being toggled.
     */
    public void toggleSelected(Exhibit exhibit) {
        viewModel.toggleSelected(exhibit);
        selectedText.setText(viewModel.getSelectedExhibits().size() + " Exhibits Selected");
    }
}