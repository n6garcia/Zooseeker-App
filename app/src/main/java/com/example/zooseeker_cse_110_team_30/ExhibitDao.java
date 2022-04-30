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
     * @see "https://www.sqlite.org/lang_expr.html"
     */
    @Query("SELECT * FROM exhibits WHERE kind='exhibit' " + //only return exhibits, no gates etc
            " AND (name LIKE ('%' || :query || '%')" + //name pattern match (|| = concatenate)
            " OR tags LIKE ('%' || :query || '%'))" + //tags pattern match (|| = concatenate)
            " ORDER BY name ASC") //order alphabetically
    List<Exhibit> getSearch(String query);

    //TODO figure out if we can combine getAll() and getAllLive()
    /**
     * Accessor for retrieving all Exhibits unconditionally.
     * @return A list of every Exhibit in this DAO.
     */
    @Query("SELECT * FROM exhibits")
    List<Exhibit> getAll();

    /**
     * Accessor for live exhibits for use in LiveData.
     * @return all exhibits currently in the DAO, sorted alphabetically by name.
     */
    @Query("SELECT * FROM exhibits WHERE kind='exhibit' ORDER BY name ASC")
    LiveData<List<Exhibit>> getAllLive();

    /**
     * Inserts all Exhibits from a list into the DAO.
     * @param exhibits The List of Exhibits to add to the DAO.
     * @return A List of the IDs of every Exhibit inserted into the DAO.
     * @see "https://androidx.de/androidx/room/EntityInsertionAdapter.html#insertAndReturnIdsList(java.util.Collection%3C?%20extends%20T%3E)"
     */
    @Insert
    List<Long> insertAll(List<Exhibit> exhibits);

    /**
     * Updater for Exhibits in this DAO.
     * @param exhibit The Exhibit to update, containing updated
     * @return The number of rows in the DAO affected by this update operation.
     * @see "https://"androidx.de/androidx/room/EntityDeletionOrUpdateAdapter.html#handle(T)"
     */
    @Update
    int update(Exhibit exhibit);
}
