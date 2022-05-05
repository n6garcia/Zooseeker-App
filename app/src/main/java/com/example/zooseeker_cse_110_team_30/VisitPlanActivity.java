package com.example.zooseeker_cse_110_team_30;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class VisitPlanActivity extends AppCompatActivity {
    private Button directionsButton; // directions button


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_plan);

        this.directionsButton = this.findViewById(R.id.directions_btn); // get plan button from layout
        directionsButton.setOnClickListener(this::onDirectionsButtonClicked);
    }

    public void onDirectionsButtonClicked(View view){
        Intent intent = new Intent(this, DirectionsActivity.class);
        startActivity(intent);
    }
}