package com.example.zooseeker_cse_110_team_30;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.location.Location;
import android.widget.Switch;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class MS2US1IntegrationTest {
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

        Exhibit testExhibit = exhibitDao.get("koi");
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
            activity.userCurrentExhibit = exhibitDao.get("entrance_exit_gate");
            TextView animalExhibit = activity.findViewById(R.id.exhibit_name);
            assertEquals(animalExhibit.getText().toString(), "Koi Fish");
            TextView dir = activity.findViewById(R.id.directions_text);
            assertEquals("Proceed down Gate Path\n" +
                    "Then down Front Street\nThen down Terrace Lagoon Loop\n\n" +
                    "Arriving in 60 ft", dir.getText().toString());
            Switch s = activity.findViewById(R.id.detailed_directions_switch);
            s.performClick();
            assertEquals("Proceed on Gate Path 10 ft towards Front Street " +
                    "/ Treetops Way\nProceed on Front Street 30 ft towards Front Street" +
                    " / Terrace Lagoon Loop (South)\nProceed on Terrace Lagoon Loop 20 ft " +
                    "to Koi Fish\n\nArriving in 60 ft", dir.getText().toString());
        });
    }

}
