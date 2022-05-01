package com.example.zooseeker_cse_110_team_30;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ExhibitClassTester {
    private Exhibit e1;
    private Exhibit e2;
    private Exhibit e3;

    @Before
    public void setUp() {
         e1 = new Exhibit("penguins", "exhibit", "Penguins", "penguins,birds,arctic");
         e2 = new Exhibit("owls", "exhibit", "Owls", "owls,birds,reptile");
    }

    @Test
    public void testConstructor() {
        assertEquals(e1.identity, "penguins");
        assertEquals(e1.kind, "exhibit");
        assertEquals(e1.name, "Penguins");
        assertFalse(e1.selected);
        assertEquals(e1.tags, "penguins,birds,arctic");

        assertEquals(e2.identity, "owls");
        assertEquals(e2.kind, "exhibit");
        assertEquals(e2.name, "Owls");
        assertFalse(e2.selected);
        assertEquals(e2.tags, "owls,birds,reptile");
    }

    @Test
    public void testEquals() {
        e3 = new Exhibit("penguins", "exhibit", "Penguins", "penguins,birds,arctic");
        assertTrue(e1.equals(e3));
        assertFalse(e2.equals(e3));
    }
}
