package com.example.zooseeker_cse_110_team_30;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    private ExhibitViewModel viewModel;
    private ImageButton searchButton;
    private EditText searchBar;
    private ExhibitAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this)
                .get(ExhibitViewModel.class);

        adapter = new ExhibitAdapter();
        adapter.setHasStableIds(true);
        adapter.setOnCheckBoxClickedHandler(viewModel::toggleSelected);
        viewModel.getExhibits().observe(this, adapter::setExhibits);

        recyclerView = findViewById(R.id.animal_exhibit_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        this.searchBar = this.findViewById(R.id.search_bar);
        this.searchButton = this.findViewById(R.id.search_button);

        searchButton.setOnClickListener(this::onSearchButtonClicked);
        //adapter.setExhibits(Exhibit.loadJSON(this, "sample_node_info.json"));
    }

    public void onSearchButtonClicked(View view) {
        String text = searchBar.getText().toString();

        List<Exhibit> searchResults = viewModel.query(text);
        adapter.setExhibits(searchResults);

        /* debug messages
        System.out.println("Search text: \"" + text + "\"");
        System.out.println(searchResults.toString()); */
    }
}