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

/*
    @Test
    public void testSmallList() {
        List<Exhibit> toVisit = new ArrayList<>();
        toVisit.add(new Exhibit("gorillas", "exhibit", "Gorillas",
                "gorilla,monkey,ape,mammal"));
        List<List<IdentifiedWeightedEdge>> actual = dir.findShortestRoute(toVisit); //TODO fix static

        ArrayList<ArrayList<String>> expected = new ArrayList<ArrayList<String>>();
        ArrayList<String> firstDir = new ArrayList<String>();
        firstDir.add("(entrance_exit_gate :edge-0: entrance_plaza)");
        firstDir.add("(entrance_plaza :edge-1: gorillas)");
        ArrayList<String> secondDir = new ArrayList<String>();
        secondDir.add("(entrance_plaza :edge-1: gorillas)");
        secondDir.add("(entrance_exit_gate :edge-0: entrance_plaza)");

        expected.add(firstDir);
        expected.add(secondDir);
        ArrayList<ArrayList<String>> actualString = new ArrayList<ArrayList<String>>();
        for(int i = 0; i < actual.size(); i++) {
            for(int j = 0; j < actual.get(i).size(); j++) {
                assertEquals(expected.get(i).get(j), actual.get(i).get(j).toString());
                System.out.println(actual.get(i).get(j).toString());
            }
        }
        System.out.println(dir.getGraph().removeVertex("r")); //TODO fix static
    }

    @Test
    public void testSingleExhibitRoute(){
        List<List<IdentifiedWeightedEdge>> actualRoute = dir.findShortestRoute(singleExhibit); //TODO fix static

        String actual = "";
        // Directions to first exhibit
        for (IdentifiedWeightedEdge e : actualRoute.get(0)) {

            actual += Directions.getEdgeInfo().get(e.getId()).street + " ";
            actual += Directions.getVertexInfo()
                    .get(Directions.getGraph().getEdgeSource(e).toString()).name + " ";
            actual += Directions.getVertexInfo()
                    .get(Directions.getGraph().getEdgeTarget(e).toString()).name + "\n";
        }

        String expected = "";

        // Directions to first exhibit
        expected += "Entrance Way Entrance and Exit Gate Entrance Plaza\n";
        expected += "Reptile Road Entrance Plaza Alligators\n";
        expected += "Sharp Teeth Shortcut Alligators Lions\n";
        expected += "Africa Rocks Street Lions Elephant Odyssey\n";

        // Directions back to entrance
        assertEquals(actual, expected);
    }


    @Test
    public void test() {
        List<IdentifiedWeightedEdge> result = dir.findShortestPath("elephant_odyssey","entrance_exit_gate");
        for(IdentifiedWeightedEdge e:result) {
            System.out.println(e);
        }
    }
*/
}
