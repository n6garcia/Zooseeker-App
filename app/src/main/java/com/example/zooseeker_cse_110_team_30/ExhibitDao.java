package com.example.zooseeker_cse_110_team_30;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExhibitDao {

    @Query("SELECT * FROM `exhibit` WHERE `id`=:id")
    Exhibit get(long id);

    @Query("SELECT * FROM `exhibit` WHERE `identity`=:name")
    Exhibit getName(String name);

    @Query("SELECT * FROM `exhibit` WHERE :tag IN (`tags`) ORDER BY `name` ASC")
    List<Exhibit> getTag(String tag);

    @Insert
    List<Long> insertAll(List<Exhibit> exhibit);
}
