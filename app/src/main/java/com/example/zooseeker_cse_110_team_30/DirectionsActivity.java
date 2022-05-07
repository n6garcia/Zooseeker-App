package com.example.zooseeker_cse_110_team_30;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DirectionsActivity extends AppCompatActivity {
    private TextView exhibitName;
    private TextView directionsText;
    private TextView nextText;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        this.exhibitName = this.findViewById(R.id.exhibit_name);
        this.directionsText = this.findViewById(R.id.directions_text);
        this.nextText = this.findViewById(R.id.next_text);

        // Set exhibit name text color to black
        exhibitName.setTextColor(getResources().getColor(R.color.black));

        // set up next button click
        this.nextButton = this.findViewById(R.id.next_btn); //get search button from layout
        nextButton.setOnClickListener(this::onNextButtonClicked);


        //TODO write code for setting appropriate exhibit details
        exhibitName.setText("BABOON");
        directionsText.setText("Proceed from Front Street down Treetops Way testing testing");
        nextText.setText("(Hippopotamus, 100ft)");
    }

    public void onNextButtonClicked(View view){
        //TODO write next exhibit click
        exhibitName.setText("HIPPOPOTAMUS");
        directionsText.setText("Proceed from Front eee Treetops Way testing testing");
        nextText.setText("(example, 100ft)");
    }


}