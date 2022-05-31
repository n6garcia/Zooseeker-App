package com.example.zooseeker_cse_110_team_30;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class MS2US5IntegrationTest {
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
    public void testThreeExhibitPlan() {

        Exhibit siamang = exhibitDao.get("siamang");
        siamang.selected = true;
        exhibitDao.update(siamang);

        Exhibit orangutan = exhibitDao.get("orangutan");
        orangutan.selected = true;
        exhibitDao.update(orangutan);

        Exhibit koi = exhibitDao.get("koi");
        koi.selected = true;
        exhibitDao.update(koi);

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
            assertEquals(animalExhibit.getText().toString(), "Siamangs");

            Location loc = new Location("");
            loc.setLatitude(32.72624997716322);
            loc.setLongitude(-117.15599314253906);
            activity.locationChangedHandler(loc);

            activity.alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).performClick();
            assertEquals(animalExhibit.getText().toString(), "Siamangs");

            Button skipButton = activity.findViewById(R.id.skip_button);
            skipButton.performClick();

            assertEquals(animalExhibit.getText().toString(), "Koi Fish");
        });
    }
}
