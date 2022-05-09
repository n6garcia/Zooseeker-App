package com.example.zooseeker_cse_110_team_30;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.config.AndroidConfigurer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@RunWith(AndroidJUnit4.class)
public class ExhibitDatabaseTest {
    private ExhibitDao dao;
    private ExhibitDatabase db;
    private List<Exhibit> two_exhibit_list;

    @Before
    public void createDb() {

        // Application provider generates a mocked application
        Context context = ApplicationProvider.getApplicationContext();

        // Ask room to build an in-memory database
        db = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries() // Allow room to make queries on main thread without warnings
                .build();
        dao = db.exhibitDao();

        // List initialization
        two_exhibit_list = new ArrayList<>();

        Exhibit e1 = new Exhibit("penguins", "exhibit", "Penguins", "penguins,birds,arctic");
        Exhibit e2 = new Exhibit("owls", "exhibit", "Owls", "owls,birds,reptile");

        two_exhibit_list.add(e1);
        two_exhibit_list.add(e2);
    }

    /**
     * Used to clear the database of the current entries
     */
    private void clearDB() {
        List<Exhibit> curr_exhibits = dao.getAll();
        for (Exhibit exhibit : curr_exhibits) {
            dao.delete(exhibit);
        }
    }

    /**
     * Test if two unique database entries are assigned unique IDs
     *
     * Unit test for User Story 1
     */
    @Test
    public void testInsert() {
        clearDB();

        // NOTE: Elements will be inserted into the database in the order that
        // they appear in the argument list. So ids.get(0) will store the id of penguin
        List<Long> ids = dao.insertAll(two_exhibit_list);
        assertNotEquals(ids.get(0), ids.get(1));
    }

    /**
     * Test if exhibit information is being stored in our database correctly
     *
     * Unit test for User Story 1/User Story 3
     */
    @Test
    public void testExhibitStoring() {
        clearDB();

        List<Long> ids = dao.insertAll(two_exhibit_list);

        Exhibit e1 = dao.get(ids.get(0));
        assertEquals((long) ids.get(0), e1.id);
        assertEquals(dao.get(ids.get(0)).identity, e1.identity);
        assertEquals(dao.get(ids.get(0)).kind, e1.kind);
        assertEquals(dao.get(ids.get(0)).name, e1.name);
        assertFalse(dao.get(ids.get(0)).selected); // selected should be initially false
        assertEquals(dao.get(ids.get(0)).tags, e1.tags);

        Exhibit e2 = dao.get(ids.get(1));
        assertEquals((long) ids.get(1), e2.id);
        assertEquals(dao.get(ids.get(1)).identity, e2.identity);
        assertEquals(dao.get(ids.get(1)).kind, e2.kind);
        assertEquals(dao.get(ids.get(1)).name, e2.name);
        assertFalse(dao.get(ids.get(1)).selected);
        assertEquals(dao.get(ids.get(1)).tags, e2.tags);
    }

    /**
     * Test if getSearch correctly returns the target exhibit if searching by
     * exact match
     *
     * Unit test for User Story 1
     */
    @Test
    public void testExactSearch() {
        clearDB();
        List<Long> ids = dao.insertAll(two_exhibit_list);
        Exhibit e1 = dao.get(ids.get(0));

        // Search explicitly for penguins
        List<Exhibit> query_result = dao.getSearch("Penguins");

        assertEquals(query_result.get(0).id, e1.id);
        assertEquals(query_result.get(0).identity, e1.identity);
        assertEquals(query_result.get(0).kind, e1.kind);
        assertEquals(query_result.get(0).name, e1.name);
        assertFalse(query_result.get(0).selected); // selected should be initially false
        assertEquals(query_result.get(0).tags, e1.tags);
    }

    /**
     * Test if getSearch correctly returns the target exhibit(s) if searching
     * by tags
     *
     * Unit test for User Story 3
     */
    @Test
    public void testTagSearch() {
        clearDB();
        List<Long> ids = dao.insertAll(two_exhibit_list);

        // Search for exhibits with "Birds" tag
        List<Exhibit> query_result = dao.getSearch("Birds");

        Exhibit penguin_e = dao.get(ids.get(0));
        Exhibit owl_e = dao.get(ids.get(1));

        // Owl should be first in the query result (O < P)
        assertEquals(query_result.get(0).id, owl_e.id);
        assertEquals(query_result.get(0).identity, owl_e.identity);
        assertEquals(query_result.get(0).kind, owl_e.kind);
        assertEquals(query_result.get(0).name, owl_e.name);
        assertFalse(query_result.get(0).selected); // selected should be initially false
        assertEquals(query_result.get(0).tags, owl_e.tags);

        assertEquals(query_result.get(1).id, penguin_e.id);
        assertEquals(query_result.get(1).identity, penguin_e.identity);
        assertEquals(query_result.get(1).kind, penguin_e.kind);
        assertEquals(query_result.get(1).name, penguin_e.name);
        assertFalse(query_result.get(1).selected); // selected should be initially false
        assertEquals(query_result.get(1).tags, penguin_e.tags);
    }

    /**
     * Test if changing selected value updates exhibit object in database accordingly
     *
     * Unit Test for User Story 2
     */
    @Test
    public void testSelected() {
        clearDB();
        List<Long> ids = dao.insertAll(two_exhibit_list);
        Exhibit e1 = dao.get(ids.get(0));
        e1.selected = true;
        dao.update(e1);
        assertEquals(true, dao.get(ids.get(0)).selected);
    }

    @Test
    public void testStressTextPlan()
    {

    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }
}
