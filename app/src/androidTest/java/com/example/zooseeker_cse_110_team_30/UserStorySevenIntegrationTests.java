package com.example.zooseeker_cse_110_team_30;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class UserStorySevenIntegrationTests {
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
    public void testOneSelectedPlan()
    {
        Exhibit gorilla = exhibitDao.get("gorillas");
        gorilla.selected = true;
        exhibitDao.update(gorilla);

        ActivityScenario<VisitPlanActivity> scenario
                = ActivityScenario.launch(VisitPlanActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;

            RecyclerView.ViewHolder VH = recyclerView.findViewHolderForAdapterPosition(1);
            long id = VH.getItemId();
            assertEquals("Gorillas", exhibitDao.get(id).name);
        });
    }
    @Test
    public void testThreePlan()
    {
        Exhibit gorilla = exhibitDao.get("gorillas");
        gorilla.selected = true;
        exhibitDao.update(gorilla);

        Exhibit lion = exhibitDao.get("lions");
        lion.selected = true;
        exhibitDao.update(lion);

        Exhibit gators = exhibitDao.get("gators");
        gators.selected = true;
        exhibitDao.update(gators);

        ActivityScenario<VisitPlanActivity> scenario
                = ActivityScenario.launch(VisitPlanActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;

            RecyclerView.ViewHolder VH = recyclerView.findViewHolderForAdapterPosition(1);
            long id = VH.getItemId();
            assertEquals("Alligators", exhibitDao.get(id).name);

            VH = recyclerView.findViewHolderForAdapterPosition(2);
            id = VH.getItemId();
            assertEquals("Lions", exhibitDao.get(id).name);

            VH = recyclerView.findViewHolderForAdapterPosition(3);
            id = VH.getItemId();
            assertEquals("Gorillas", exhibitDao.get(id).name);
        });
    }
}
