package com.example.zooseeker_cse_110_team_30;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@Entity(tableName = "exhibit")
public class Exhibit {
    // Fields
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String identity;
    public String kind;
    public String name;
    public boolean selected;
    public String tags;

    public Exhibit(@NonNull String identity, String kind, String name, String tags) {
        this.identity = identity;
        this.kind = kind;
        this.name = name;
        this.selected = false;
        this.tags = tags;
    }

    public static List<Exhibit> loadJSON(Context context, String path) {
        try {
            InputStream input = context.getAssets().open(path);
            Reader reader = new InputStreamReader(input);
            Gson gson = new Gson();
            Type type = new TypeToken<List<Exhibit>>() {
            }.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public String toString() {
        return "Exhibit{" +
                "id=" + id +
                ", identification='" + identity + '\'' +
                ", kind='" + kind + '\'' +
                ", name='" + name + '\'' +
                ", selected=" + selected +
                ", tags=" + tags +
                '}';
    }

    /**
     * Overwritten equality operator for Exhibit that compares member variables.
     *
     * @param e the Exhibit object to compare to
     * @return true if the identity, kind, name, and tags of the two Exhibits are equal.
     */
    @Override
    public boolean equals(Object e) {
        if(e.getClass() != Exhibit.class) {
            return false; //return false if not Exhibit object
        }
        //comparison operators for important fields
        return this.identity.equals(((Exhibit) e).identity)
                &&  this.kind.equals(((Exhibit) e).kind)
                &&  this.name.equals(((Exhibit) e).name)
                &&  this.tags.equals(((Exhibit) e).tags);
    }
}
