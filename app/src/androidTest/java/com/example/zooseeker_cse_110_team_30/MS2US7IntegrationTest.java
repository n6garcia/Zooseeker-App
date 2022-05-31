package com.example.zooseeker_cse_110_team_30;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class MS2US7IntegrationTest {
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
    public void testClearButtonDisplay() {
        ActivityScenario<MainActivity> scenario
                = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            Button clearButton = activity.findViewById(R.id.clear_button);
            assertFalse(activity.alertDialog.isShowing());

            clearButton.performClick();
            assertTrue(activity.alertDialog.isShowing());
        });
    }

    @Test
    public void testClearButtonFunctionality() {

        Exhibit testExhibit = exhibitDao.get("koi");
        testExhibit.selected = true;
        exhibitDao.update(testExhibit);

        Exhibit testExhibit1 = exhibitDao.get("hippo");
        testExhibit1.selected = true;
        exhibitDao.update(testExhibit1);

        ActivityScenario<MainActivity> scenario
                = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            Button clearButton = activity.findViewById(R.id.clear_button);
            assertEquals(2, activity.viewModel.getSelectedExhibits().size());

            //cancel clear
            clearButton.performClick();
            activity.alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).performClick();
            assertEquals(2, activity.viewModel.getSelectedExhibits().size());
            //assertFalse(activity.alertDialog.isShowing());

            //authorize clear
            //stupid hack solution for stupid threads UGH
            clearButton.performClick();
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    activity.alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                }
            });
            activity.findViewById(R.id.clear_button).getRootView().post(new Runnable() {
                public void run() {
                    assertEquals(0, activity.viewModel.getSelectedExhibits().size());
                }
            });
            //assertFalse(activity.alertDialog.isShowing());
        });
    }
}
