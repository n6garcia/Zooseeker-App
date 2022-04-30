package com.example.zooseeker_cse_110_team_30;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteCompat;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * The Room Database class for Exhibits.
 */
@Database(entities = {Exhibit.class}, version = 1)
public abstract class ExhibitDatabase extends RoomDatabase {
    private static ExhibitDatabase singleton = null; //TODO problematic? see lab 5
    public abstract ExhibitDao exhibitDao(); //implemented in ExhibitDatabase_Impl

    /**
     * The Getter method for the singleton.
     * @param context A Context to use if creating a new database.
     * @return The singleton, or a newly created database if the singleton does not exist.
     */
    public synchronized static ExhibitDatabase getSingleton(Context context){
        if(singleton == null){ //database does not exist, make a new one w/ given context
            singleton = ExhibitDatabase.makeDatabase(context);
        }
        return singleton;
    }

    /**
     * Makes a database, loads the specified JSON, and inserts all Exhibits into the database.
     * @param context The context from which to pull information about the environment.
     * @return An ExhibitDatabase containing a DAO which contains all Exhibits from the JSON.
     */
    private static ExhibitDatabase makeDatabase(Context context){
        return Room.databaseBuilder(context, ExhibitDatabase.class, "zoo_exhibits.db") //name
                .allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(() -> {
                            List<Exhibit> exhibits = Exhibit
                                    .loadJSON(context, "sample_node_info.json"); //TODO JSON
                            getSingleton(context).exhibitDao().insertAll(exhibits); //insert DAO
                        });
                    }
                })
                .build();
    }

    /**
     * Testing setter for the singleton, allows for the singleton to be changed during testing.
     * @param testDatabase The new testing singleton.
     */
    @VisibleForTesting
    public static void injectTestDatabase(ExhibitDatabase testDatabase){
        if (singleton != null){
            singleton.close(); //close existing singleton, if it exists
        }
        singleton = testDatabase;
    }
}

