package com.example.zooseeker_cse_110_team_30;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UserStoryNineIntegrationTests {

    ExhibitDatabase testDb;
    ExhibitDao exhibitDao;

    /**
     * Resets database before each test to be contents of JSON asset
     */
    @Before
    public void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries()
                .build();
        ExhibitDatabase.injectTestDatabase(testDb);

        List<Exhibit> exhibits = Exhibit.loadJSON(context, "node_info.json");
        Directions.setDatabase(context, testDb);
        exhibitDao = Directions.getDao();

        exhibitDao.insertAll(exhibits);
    }

    @Test
    public void testSingleExhibitPlan() {
        Exhibit testExhibit = exhibitDao.get("gorilla");
        testExhibit.selected = true;
        exhibitDao.update(testExhibit);

        ActivityScenario<VisitPlanActivity> scenario
                = ActivityScenario.launch(VisitPlanActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        ActivityScenario<DirectionsActivity> scenario_dir
                = ActivityScenario.launch(DirectionsActivity.class);
        scenario_dir.moveToState(Lifecycle.State.CREATED);
        scenario_dir.moveToState(Lifecycle.State.STARTED);
        scenario_dir.moveToState(Lifecycle.State.RESUMED);

        scenario_dir.onActivity(activity -> {
            Button nextButton = activity.findViewById(R.id.next_button);
            nextButton.performClick();

            TextView exhibitName = activity.findViewById(R.id.exhibit_name);
            assertEquals(exhibitName.getText().toString(), "Entrance and Exit Gate");
        });
    }

    @Test
    public void testAllExhibitPlan() {
        List<Exhibit> currExhibits = exhibitDao.getAllExhibits();
        List<String> expectedExhibits = new ArrayList<>();
        Exhibit tempExhibit;
        for (int i = 0; i < currExhibits.size(); i++) {
            tempExhibit = currExhibits.get(i);
            tempExhibit.selected = true;
            exhibitDao.update(tempExhibit);
        }

        expectedExhibits.add("Siamangs");
        expectedExhibits.add("Orangutans");
        expectedExhibits.add("Toucan");
        expectedExhibits.add("Blue Capped Motmot");
        expectedExhibits.add("Bali Mynah");
        expectedExhibits.add("Emerald Dove");
        expectedExhibits.add("Fern Canyon");
        expectedExhibits.add("Hippos");
        expectedExhibits.add("Crocodiles");
        expectedExhibits.add("Spoonbill");
        expectedExhibits.add("Gorillas");
        expectedExhibits.add("Capuchin Monkeys");
        expectedExhibits.add("Flamingos");
        expectedExhibits.add("Koi Fish");
        expectedExhibits.add("Entrance and Exit Gate");

        ActivityScenario<VisitPlanActivity> scenario
                = ActivityScenario.launch(VisitPlanActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        ActivityScenario<DirectionsActivity> scenario_dir
                = ActivityScenario.launch(DirectionsActivity.class);
        scenario_dir.moveToState(Lifecycle.State.CREATED);
        scenario_dir.moveToState(Lifecycle.State.STARTED);
        scenario_dir.moveToState(Lifecycle.State.RESUMED);

        scenario_dir.onActivity(activity -> {

            TextView textView = activity.findViewById(R.id.exhibit_name);
            Button nextButton = activity.findViewById(R.id.next_button);

            for (int i = 0; i < expectedExhibits.size(); i++) {
                assertEquals(expectedExhibits.get(i), textView.getText().toString());
                nextButton.performClick();
            }
        });
    }
}
