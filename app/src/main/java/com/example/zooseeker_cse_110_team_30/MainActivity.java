package com.example.zooseeker_cse_110_team_30;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    public ExhibitViewModel viewModel;
    public ImageButton searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this)
                .get(ExhibitViewModel.class);

        ExhibitAdapter adapter = new ExhibitAdapter();
        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.animal_exhibit_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        this.searchButton = this.findViewById(R.id.search_button);
        searchButton.setOnClickListener(this::onSearchButtonClicked);

        adapter.setExhibitItems(Exhibit.loadJSON(this, "sample_node_info.json"));
    }

    public void onSearchButtonClicked(View view) {
        EditText searchBar = view.findViewById(R.id.search_bar);
        String text = searchBar.getText().toString();
        Exhibit searchResult = viewModel.query(text);
        if(searchResult == null) {
            return;
        }
    }
}