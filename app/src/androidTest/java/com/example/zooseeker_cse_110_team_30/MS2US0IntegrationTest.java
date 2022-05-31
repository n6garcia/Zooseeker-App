package com.example.zooseeker_cse_110_team_30;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.location.Location;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class MS2US0IntegrationTest {
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
    public void testMockReplan() {
        Exhibit testExhibit1 = exhibitDao.get("capuchin");
        Exhibit testExhibit2 = exhibitDao.get("gorilla");
        testExhibit1.selected = true;
        testExhibit2.selected = true;
        exhibitDao.update(testExhibit1);
        exhibitDao.update(testExhibit2);

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
            assertEquals(animalExhibit.getText().toString(), "Capuchin Monkeys");

            assertFalse(activity.alertDialog.isShowing());

            EditText lat_view = activity.findViewById(R.id.mock_lat);
            EditText lon_view = activity.findViewById(R.id.mock_lon);

            // Toggle mock switch
            Switch mock_switch = activity.findViewById(R.id.mock_location_switch);
            mock_switch.performClick();

            // Set mocked location to be exactly the gorillas exhibit
            lat_view.setText("32.74711745394194");
            lon_view.setText("-117.18047982358976");

            // Just need an empty location to be passed into handler, it will be ignored anyway.
            // The mock location will be used
            Location loc = new Location("");
            activity.locationChangedHandler(loc); // Call our location handler

            assertTrue(activity.alertDialog.isShowing());
        });
    }
}
