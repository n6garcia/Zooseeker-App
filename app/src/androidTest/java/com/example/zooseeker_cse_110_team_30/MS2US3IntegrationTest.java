package com.example.zooseeker_cse_110_team_30;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.location.Location;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MS2US3IntegrationTest {

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
    public void testSimplePrevious() {
        Exhibit testExhibit = exhibitDao.get("koi");
        testExhibit.selected = true;
        exhibitDao.update(testExhibit);

        Exhibit testExhibit2 = exhibitDao.get("gorilla");
        testExhibit2.selected = true;
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
            TextView animalExhibit = activity.findViewById(R.id.exhibit_name); // Text view of nav exhibit

            Switch mockSwitch = activity.findViewById(R.id.mock_location_switch);
            mockSwitch.performClick(); // Set default location to entrance exit gate
            assertEquals("Koi Fish", animalExhibit.getText().toString());

            Button nextButton = activity.findViewById(R.id.next_button);
            nextButton.performClick();

            assertEquals("Gorillas", animalExhibit.getText().toString());

            Button prevButton = activity.findViewById(R.id.previous_button);
            prevButton.performClick();

            assertEquals("Koi Fish", animalExhibit.getText().toString());
        });
    }
}
