package com.example.zooseeker_cse_110_team_30;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * DAO - Data Access Object - Allows for the storage and retrieval of objects.
 */
@Dao
public interface ExhibitDao {

    /**
     * Accessor for retrieving an Exhibit with a specific ID.
     * @param id The ID of the Exhibit to get.
     * @return The Exhibit with the exactly matching ID.
     */
    @Query("SELECT * FROM exhibits WHERE id=:id")
    Exhibit get(long id);

    /**
     * Accessor for retrieving all Exhibits matching a name or tag substring.
     * @param query The String to search for.
     * @return All Exhibits where the query is a substring of name or tags, ordered alphabetically.
     */
    @Query("SELECT * FROM exhibits WHERE kind='exhibit' " + //only return exhibits, no gates etc
            " AND (name LIKE ('%' || :query || '%')" + //name pattern match (|| = concatenate)
            " OR tags LIKE ('%' || :query || '%'))" + //tags pattern match (|| = concatenate)
            " ORDER BY name ASC") //order alphabetically
    List<Exhibit> getSearch(String query);

    @Query("SELECT * FROM exhibits WHERE kind='exhibit'")
    List<Exhibit> getAll();

    @Query("SELECT * FROM exhibits WHERE kind='exhibit' ORDER BY name ASC")
    LiveData<List<Exhibit>> getAllLive();

    @Insert
    List<Long> insertAll(List<Exhibit> exhibit);

    @Update
    int update(Exhibit exhibit);
}
