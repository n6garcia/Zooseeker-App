package com.example.zooseeker_cse_110_team_30;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

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
        Directions.setDatabase(context, testDb);
        exhibitDao = Directions.getDao();

        exhibitDao.insertAll(exhibits);
    }
    @Test
    public void testDb() {
        Exhibit e = exhibitDao.get("toucan");
        System.out.println(e.id);
        System.out.println(e.identity);
        System.out.println(e.group_id);
        System.out.println(e.kind);
        System.out.println(e.name);
        System.out.println(e.tags);
        System.out.println(e.latitude);
        System.out.println(e.longitude);

    }
    @Test
    public void testOneSelectedPlan() {
        Exhibit gorilla = exhibitDao.get("gorilla");
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
        Exhibit gorilla = exhibitDao.get("gorilla");
        gorilla.selected = true;
        exhibitDao.update(gorilla);

        Exhibit dove = exhibitDao.get("dove");
        dove.selected = true;
        exhibitDao.update(dove);

        Exhibit mynah = exhibitDao.get("mynah");
        mynah.selected = true;
        exhibitDao.update(mynah);

        ActivityScenario<VisitPlanActivity> scenario
                = ActivityScenario.launch(VisitPlanActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;

            RecyclerView.ViewHolder VH = recyclerView.findViewHolderForAdapterPosition(1);
            long id = VH.getItemId();
            assertEquals("Bali Mynah", exhibitDao.get(id).name);

            VH = recyclerView.findViewHolderForAdapterPosition(2);
            id = VH.getItemId();
            assertEquals("Emerald Dove", exhibitDao.get(id).name);

            VH = recyclerView.findViewHolderForAdapterPosition(3);
            id = VH.getItemId();
            assertEquals("Gorillas", exhibitDao.get(id).name);
        });
    }
}
