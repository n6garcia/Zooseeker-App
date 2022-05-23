package com.example.zooseeker_cse_110_team_30;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
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
import java.util.Collections;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class UserStoryTwoIntegrationTests {
    ExhibitDatabase testDb;
    ExhibitDao exhibitDao;

    private static void forceLayout(RecyclerView recyclerView) {
        recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        recyclerView.layout(0, 0, 1080, 2280);
    }

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
    public void testThreeSelectedExhibits() {
        ActivityScenario<MainActivity> scenario
                = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;
            ArrayList<Exhibit> expectedSelectedList = new ArrayList<Exhibit>();
            for(int i = 0; i < 3; i++) {
                RecyclerView.ViewHolder VH = recyclerView.findViewHolderForAdapterPosition(i);
                assertNotNull(VH);
                long id = VH.getItemId();
                expectedSelectedList.add(exhibitDao.get(id));
                VH.itemView.findViewById(R.id.selected).performClick();
            }

            for(int i = 0; i < 3; i++) {
                assertTrue(expectedSelectedList.contains(activity.viewModel.getSelectedExhibits().get(i)));
            }
        });
    }

    @Test
    public void testNoSelectedExhibits() {
        ActivityScenario<MainActivity> scenario
                = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {

            // No exhibits selected
            assertEquals(activity.viewModel.getSelectedExhibits().size(), 0);
        });
    }

    @Test
    public void test10SelectedExhibits() {
        ActivityScenario<MainActivity> scenario
                = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        ArrayList<Exhibit> expectedSelectedList = new ArrayList<Exhibit>();
        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;
            List<Exhibit> allExhibits = activity.viewModel.getAllExhibits();
            for(int i = 0; i < 10; i++) {
                RecyclerView.ViewHolder VH = recyclerView.findViewHolderForAdapterPosition(i);
                System.out.println();
                assertNotNull(VH);
                long id = VH.getItemId();
                expectedSelectedList.add(exhibitDao.get(id));
                VH.itemView.findViewById(R.id.selected).performClick();
            }

            for(int i = 0; i < 10; i++) {
                assertTrue(expectedSelectedList.contains(activity.viewModel.getSelectedExhibits().get(i)));
            }
        });
    }
}
