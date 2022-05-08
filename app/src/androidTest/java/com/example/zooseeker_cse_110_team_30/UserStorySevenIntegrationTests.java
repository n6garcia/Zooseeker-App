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

        List<Exhibit> exhibits = Exhibit.loadJSON(context, "sample_node_info.json");
        exhibitDao = testDb.exhibitDao();
        exhibitDao.insertAll(exhibits);
    }

    @Test
    public void testStressAllExhibitsTextPlan()
    {
        ActivityScenario<MainActivity> scenario
                = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);


        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;

            EditText searchBar = activity.findViewById(R.id.search_bar);
            ImageButton searchButton = activity.findViewById(R.id.search_button);

            List<Exhibit> allExhibits = activity.viewModel.getAllExhibits();

            //select all exhibits
            for(int i = 0; i < allExhibits.size(); i++)
            {
                searchBar.setText(allExhibits.get(i).name);
                searchButton.performClick();

                //There should be only one search result since this is specific
                RecyclerView.ViewHolder VH = recyclerView.findViewHolderForAdapterPosition(0);
                assertNotNull(VH);
                long id = VH.getItemId();
                VH.itemView.findViewById(R.id.selected).performClick();
            }

            Button planButton = activity.findViewById(R.id.plan_button);
            planButton.performClick();

//        ActivityScenario<VisitPlanActivity> scenarioVisit
//                = ActivityScenario.launch(VisitPlanActivity.class);
//        scenarioVisit.moveToState(Lifecycle.State.CREATED);
//        scenarioVisit.moveToState(Lifecycle.State.STARTED);
//        scenarioVisit.moveToState(Lifecycle.State.RESUMED);
//
//        scenarioVisit.onActivity(activity -> {
//            RecyclerView recyclerView = activity.recyclerView;
//
//            int i = 0;
//            RecyclerView.ViewHolder VH;
//            List<String> expectedRoute = new ArrayList<String>();
//
//            expectedRoute.add("entrance_exit_gate");
//            expectedRoute.add("entrance_plaza");
//            expectedRoute.add("gorillas");
//            expectedRoute.add("lions");
//            expectedRoute.add("gators");
//            expectedRoute.add("elephant_odyssey");
//            expectedRoute.add("arctic_foxes");
//
//            while(recyclerView.findViewHolderForAdapterPosition(i) != null)
//            {
//                VH = recyclerView.findViewHolderForAdapterPosition(i);
//                assertEquals(VH.itemView.findViewById(R.id.exhibit_name_text).toString(), expectedRoute
//                .get(i));
//                i++;
//            }
//        });
        });
    }
}
