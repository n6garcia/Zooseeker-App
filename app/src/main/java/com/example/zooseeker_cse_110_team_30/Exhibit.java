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

/**
 * The exhibit/general location class. Each object describes a location and some of its properties
 */
@Entity(tableName = "exhibit")
public class Exhibit {
    @PrimaryKey(autoGenerate = true)
    public long id;             //unique id for this particular Exhibit

    @NonNull
    public String identity;     //internal identifier
    public String kind;         //type of location
    public String name;         //external identifier
    public boolean selected;    //added to visit plan?
    public String tags;         //object tags

    /**
     * The constructor for the Exhibit object
     * @param identity A lowercase no-space identifier for each exhibit (ie arctic_foxes).
     * @param kind Type of location. possibilities include: exhibit, intersection, gate, etc.
     * @param name The public name of the object. Should be formatted nicely (ie Arctic Foxes).
     * @param tags Tags associated with this object, formatted as a comma-separated list.
     */
    public Exhibit(@NonNull String identity, String kind, String name, String tags) {
        this.identity = identity;
        this.kind = kind;
        this.name = name;
        this.selected = false; //default not selcected
        this.tags = tags;
    }

    /**
     * Given a JSON file, returns the list of Exhibit objects represented by the data in the file.
     * @param context the input Context. Allows access to global information about an environment.
     * @param path The filepath to the JSON file.
     * @return A List object of Exhibits containing the data in the JSON file.
     */
    public static List<Exhibit> loadJSON(Context context, String path) {
        try {
            InputStream input = context.getAssets().open(path); //input stream to JSON file
            Reader reader = new InputStreamReader(input); //input to Gson
            Gson gson = new Gson(); //Google library for encoding/decoding JSON files
            Type type = new TypeToken<List<Exhibit>>(){}.getType(); //List<Exhibit> type
            return gson.fromJson(reader, type); //read JSON
        } catch (IOException e) { //caught error when reading file
            e.printStackTrace(); //print debug message
            return Collections.emptyList(); //return default - empty list
        }
    }

    /**
     * Overridden toString for Exhibit.
     * @return A string representation of the Exhibit containing all member variable data.
     */
    @Override @NonNull
    public String toString() {
        return "Exhibit{" +
                "id=" + id +
                ", identity='" + identity + '\'' +
                ", kind='" + kind + '\'' +
                ", name='" + name + '\'' +
                ", selected=" + selected +
                ", tags=" + tags +
                '}';
    }

    /**
     * Overridden equality operator for Exhibit that compares member Strings.
     * @param e the Exhibit object to compare to
     * @return true if the identity, kind, name, and tags of the two Exhibits are equal.
     */
    @Override
    public boolean equals(Object e) {
        if(e.getClass() != Exhibit.class) {
            return false; //return false if not Exhibit object
        }
        //comparisons for all String fields
        return this.identity.equals(((Exhibit) e).identity)
                &&  this.kind.equals(((Exhibit) e).kind)
                &&  this.name.equals(((Exhibit) e).name)
                &&  this.tags.equals(((Exhibit) e).tags);
    }
}
