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

@Database(entities = {Exhibit.class}, version = 1)
public abstract class ExhibitDatabase extends RoomDatabase {
    //from part 5
    private static ExhibitDatabase singleton = null;

    public abstract ExhibitDao exhibitDao();

    public synchronized static ExhibitDatabase getSingleton(Context context){
        if(singleton == null){
            singleton = ExhibitDatabase.makeDatabase(context);
        }
        return singleton;
    }

    private static ExhibitDatabase makeDatabase(Context context){
        return Room.databaseBuilder(context, ExhibitDatabase.class, "zoo_exhibits.db")
                .allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(() -> {
                            List<Exhibit> exhibits = Exhibit
                                    .loadJSON(context, "sample_node_info.json");
                            getSingleton(context).exhibitDao().insertAll(exhibits);
                        });
                    }
                })
                .build();
    }

    //added from 5
    @VisibleForTesting
    public static void injectTestDatabase(ExhibitDatabase testDatabase){
        if (singleton != null){
            singleton.close();
        }
        singleton = testDatabase;
    }
}

