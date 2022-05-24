package com.example.zooseeker_cse_110_team_30;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The exhibit/general location class. Each object describes a location and some of its properties.
 */
@Entity(tableName = "exhibits")
public class Exhibit {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("databaseID")
    public long id;             //unique id for this particular Exhibit

    @SerializedName("id")
    @NonNull
    public String identity;     //internal identifier
    public String kind;         //type of location
    public String name;         //external identifier
    public boolean selected;    //added to visit plan?
    public int visited;         //order of visitation, -1 if not selected/not visited
    public String tags;         //object tags

    /*TODO  every Exhibit has id, kind, name, and tags
            needed constructors:
            Non-grouped exhibit/gate/intersection: lat, long
            Grouped exhibit: group id
            Exhibit group: lat, long
    */
    public String groupId;      //exhibit group ID (MS2) //TODO add constructor
    public double latitude;     //exhibit latitude (MS2)
    public double longitude;    //exhibit longitude (MS2)

    /**
     * The constructor for the Exhibit object with a latitude and longitude
     * @param identity A lowercase no-space identifier for each exhibit (ie arctic_foxes).
     * @param groupId The id field of the exhibit group, if applicable.
     * @param kind Type of location. possibilities include: exhibit, intersection, gate, etc.
     * @param name The public name of the object. Should be formatted nicely (ie Arctic Foxes).
     * @param tags Tags associated with this object, formatted as a comma-separated list.
     * @param latitude Double representing latitude of this Exhibit, if applicable.
     * @param longitude Double representing longitude of this Exhibit, if applicable.
     */
    public Exhibit(@NonNull String identity, String groupId, String kind, String name, String tags,
                   double latitude, double longitude) {
        this.identity = identity;
        this.groupId = groupId;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
        this.latitude = latitude;
        this.longitude = longitude;

        this.selected = false; //default not selected
        this.visited = -1; //default not visited
    }

    /**
     * Given a JSON file, returns the list of Exhibit objects represented by the data in the file.
     * @param context the input Context. Allows access to global information about an environment.
     * @param path The filepath to the JSON file.
     * @return A List object of Exhibits containing the data in the JSON file.
     * @see "https://github.com/google/gson"
     * @see "https://developer.android.com/reference/android/content/Context"
     */
    public static List<Exhibit> loadJSON(Context context, String path) {
        try {
            //create database with formatted data
            InputStream input = context.getAssets().open(path); //input stream to JSON file
            Reader reader = new InputStreamReader(input); //input to Gson
            Gson gson = new Gson(); //Google library for encoding/decoding JSON files
            //create list of JsonConverterExhibit (takes in List<String> as parameter)
            Type type = new TypeToken<List<JsonConverterExhibit>>(){}.getType();
            List<JsonConverterExhibit> convertList = gson.fromJson(reader, type); //read JSON
            List<Exhibit> exhibitList = new ArrayList<>(); //final returned List
            for(JsonConverterExhibit j : convertList) { //create exhibit from JsonConverterExhibit
                exhibitList.add(new Exhibit(j.id, j.parent_id, j.kind, j.name, j.getTagString(),
                        j.lat, j.lng));
            }
            return exhibitList;
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
                ", group_id='" + groupId + '\'' +
                ", kind='" + kind + '\'' +
                ", name='" + name + '\'' +
                ", selected=" + selected +
                ", tags=" + tags +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    /**
     * Whether or not this "exhibit" is actually a group of exhibits
     * @return True if this exhibit is an exhibit group, false otherwise.
     */
    public boolean isExhibitGroup() {
        return this.kind.equals("exhibit_group");
    }

    /**
     * Overridden equality operator for Exhibit that compares member Strings.
     * @param o the Object to compare to
     * @return true if the type, groupId, identity, kind, name, and tags are equal.
     */
    @Override
    public boolean equals(Object o) {
        if(o.getClass() != Exhibit.class) {
            return false; //return false if not Exhibit object
        }
        Exhibit e = (Exhibit) o; //typecast now so we don't have to do it repeatedly
        if(this.isExhibitGroup() != e.isExhibitGroup()
            || (this.isExhibitGroup() && e.isExhibitGroup() && !this.groupId.equals(e.groupId))) {
            return false;
        }
        //comparisons for all String fields
        return this.identity.equals(e.identity)
                && this.kind.equals(e.kind)
                && this.name.equals(e.name)
                && this.tags.equals(e.tags);
        //don't mess with floating point precision
    }
}
