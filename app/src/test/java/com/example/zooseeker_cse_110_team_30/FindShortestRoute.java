package com.example.zooseeker_cse_110_team_30;

import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class FindShortestRoute {
    private Directions dir;
    private Context context;

    @Before
    public void setUp() {
       context = ApplicationProvider.getApplicationContext();
       dir = new Directions(context);
    }

    @Test
    public void testSmallList() {
        List<String> toVisit = new ArrayList<String>();
        toVisit.add("gorillas");
        List<List<IdentifiedWeightedEdge>> actual = dir.findShortestRoute(toVisit);

        ArrayList<ArrayList<String>> expected = new ArrayList<ArrayList<String>>();
        ArrayList<String> firstDir = new ArrayList<String>();
        firstDir.add("(entrance_exit_gate :e0: entrance_plaza)");
        firstDir.add("(entrance_plaza :e1: gorillas)");
        ArrayList<String> secondDir = new ArrayList<String>();
        secondDir.add("(gorillas :e1: entrance_plaza)");
        secondDir.add("(entrance_plaza :e0: entrance_exit_gate)");

        expected.add(firstDir);
        expected.add(secondDir);
        ArrayList<ArrayList<String>> actualString = new ArrayList<ArrayList<String>>();
        for(int i = 0; i < actual.size(); i++) {
            actualString.add(new ArrayList<String>());
            for(int j = 0; j < actual.get(i).size(); j++) {
                assertEquals(expected.get(i).get(j), actual.get(i).get(j).toString());
                //actualString.get(i).add(actual.get(i).get(j).toString());
                System.out.println(actual.get(i).get(j).toString());
            }
        }
    }

}
