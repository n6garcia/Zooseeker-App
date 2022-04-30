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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    public ExhibitViewModel viewModel;
    public ImageButton searchButton;
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

        recyclerView = findViewById(R.id.animal_exhibit_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        this.searchBar = this.findViewById(R.id.search_bar);
        this.searchButton = this.findViewById(R.id.search_button);
        searchButton.setOnClickListener(this::onSearchButtonClicked);

        adapter.setExhibitItems(Exhibit.loadJSON(this, "sample_node_info.json"));
    }

    public void onSearchButtonClicked(View view) {
        String text = searchBar.getText().toString();

        Exhibit searchResult = viewModel.query(text);
        List<Exhibit> allResult = viewModel.allQuery();

        List<Exhibit> exhibitList = new ArrayList<>();

        if(searchResult == null) {
            System.out.println("null");
        } else {
            System.out.println(searchResult.toString());
            exhibitList.add(searchResult);
        }
        if(allResult == null) {
            System.out.println("null");
        } else {
            System.out.println(allResult.toString());
        }
        for (int i = 0; i < allResult.size(); i++) {
            Exhibit curr = allResult.get(i);
            String raw = curr.tags;
            List<String> tagList = Arrays.asList(raw.split("\\s*,\\s*"));

            for (int j = 0; j < tagList.size(); j++) {
                String currStr = tagList.get(j);
                if (text.equals(currStr)){
                    System.out.println(currStr);
                    exhibitList.add(curr);
                }
            }

        }

        System.out.println(exhibitList.toString());

        adapter.setExhibitItems(exhibitList);

    }
}