package com.example.zooseeker_cse_110_team_30;

import static android.os.SystemClock.sleep;
import static org.junit.Assert.*;

import android.content.Context;
import android.location.Location;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class MS2US4IntegrationTest {
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

        Exhibit testExhibit1 = exhibitDao.get("hippo");
        testExhibit1.selected = true;
        exhibitDao.update(testExhibit1);

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

            assertFalse(activity.alertDialog.isShowing());

            Location loc = new Location("");
            loc.setLatitude(32.74531131120979);
            loc.setLongitude(-117.16626781198586);
            activity.locationChangedHandler(loc);

            assertTrue(activity.alertDialog.isShowing());
        });
    }

}
