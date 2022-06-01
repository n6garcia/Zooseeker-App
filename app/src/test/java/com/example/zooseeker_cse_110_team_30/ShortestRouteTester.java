package com.example.zooseeker_cse_110_team_30;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ShortestRouteTester {
    private Context context;
    private List<Exhibit> singleExhibit;
    ExhibitDatabase testDb;
    ExhibitDao exhibitDao;

    @Before
    public void setUp() {
        //context = ApplicationProvider.getApplicationContext();
        //Directions.setContext(context);

        Context context = ApplicationProvider.getApplicationContext();
        ExhibitDatabase testDb = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries()
                .build();
        ExhibitDatabase.injectTestDatabase(testDb);

        List<Exhibit> exhibits = Exhibit.loadJSON(context, "node_info.json");
        Directions.setDatabase(context, testDb);
        exhibitDao = Directions.getDao();

        exhibitDao.insertAll(exhibits);

        // Variable initialization
        /*singleExhibit = new ArrayList<>();
        singleExhibit.add(new Exhibit("elephant_odyssey", "exhibit",
                "Elephant Odyssey", "elephant,mammal,africa"));
         */
    }

    @Test
    public void testOneExhibit() {
        ExhibitDao dao = Directions.getDao();
        List<Exhibit> toVisit = new ArrayList<>();

        //toVisit.add(dao.get("gorilla"));
        Exhibit exhibit = dao.get("gorilla");
        exhibit.selected = true;
        dao.update(exhibit);

        List<Exhibit> expected = new ArrayList<>();
        expected.add(dao.get("entrance_exit_gate"));
        expected.add(dao.get("gorilla"));
        expected.add(dao.get("entrance_exit_gate"));

        //List<Exhibit> actual = Directions.findVisitPlan(toVisit);
        List<Exhibit> actual = Directions.findVisitPlan();

        for (int i = 0; i < expected.size(); i++) {
            //System.out.println("Index " + i + ": Expected: " + expected.get(i));
            //System.out.println("Actual: " + actual.get(i));
            assertEquals(expected.get(i), actual.get(i));
        }
    }
}
