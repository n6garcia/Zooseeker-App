package com.example.zooseeker_cse_110_team_30;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.provider.Settings;
import android.view.View;
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class UserStoryThreeIntegrationTests {
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

        List<Exhibit> exhibits = Exhibit.loadJSON(context, "sample_node_info.json");
        exhibitDao = testDb.exhibitDao();
        exhibitDao.insertAll(exhibits);
    }

    @Test
    public void testTagSearch() {
        String search = "africa";

        ActivityScenario<MainActivity> scenario
                = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            EditText searchText = activity.findViewById(R.id.search_bar);
            ImageButton searchButton = activity.findViewById(R.id.search_button);

            RecyclerView recyclerView = activity.recyclerView;

            searchText.setText(search);
            searchButton.performClick();

            assertEquals(recyclerView.getAdapter().getItemCount(), 2);

            long id = recyclerView.getAdapter().getItemId(0);
            Exhibit exhibit1 = exhibitDao.get(id);
            assertEquals(exhibit1.name, "Elephant Odyssey");

            id = recyclerView.getAdapter().getItemId(1);
            Exhibit exhibit2 = exhibitDao.get(id);
            assertEquals(exhibit2.name, "Lions");

        });

    }
}
