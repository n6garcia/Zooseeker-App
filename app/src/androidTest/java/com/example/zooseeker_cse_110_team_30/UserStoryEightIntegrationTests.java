package com.example.zooseeker_cse_110_team_30;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
public class UserStoryEightIntegrationTests {
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
        exhibitDao = testDb.exhibitDao();
        exhibitDao.insertAll(exhibits);
    }

    @Test
    public void testSingleExhibitPlan() {

        Exhibit testExhibit = exhibitDao.get("lions");
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

            TextView animalExhibit = activity.findViewById(R.id.exhibit_name);
            assertEquals(animalExhibit.getText().toString(), "Lions");
        });
    }

}
