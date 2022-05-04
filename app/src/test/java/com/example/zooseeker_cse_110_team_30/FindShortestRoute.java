package com.example.zooseeker_cse_110_team_30;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FindShortestRoute {
    private Directions dir;
    private Context context;

    @Before
    public void setUp() {
       context = ApplicationProvider.getApplicationContext();
    }
}
