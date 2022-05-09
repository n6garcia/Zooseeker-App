package com.example.zooseeker_cse_110_team_30;

import java.util.List;

/**
 * Utility class for loading the database, used solely for creating new Exhibit objects
 * Takes in an array of Strings from the JSON reader and outputs that array as a single String
 */
public class JsonConverterExhibit {
    public String id;     //internal identifier
    public String kind;         //type of location
    public String name;         //external identifier
    private List<String> tags;         //object tags

    /**
     * Constructor for the JsonConverterExhibit class.
     * @param id The identity String of the final Exhibit object.
     * @param kind The kind String of the final Exhibit object.
     * @param name The name String of the final Exhibit object.
     * @param tags The tags List to be converted to a String.
     */
    public JsonConverterExhibit(String id, String kind, String name, List<String> tags) {
        this.id = id;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
    }

    /**
     * Converts the List of tags to a single String through comma-separated concatenation.
     * @return A comma-separated String of tags.
     */
    public String getTagString() {
        String tagString = ""; //initialize in case no tags
        if(tags.size() > 0) { //check to prevent OOB errors
            for (int i = 0; i < tags.size() - 1; i++) {
                tagString = tagString + tags.get(i) + ","; //add each tag + a comma
            }
            tagString = tagString + tags.get(tags.size() - 1); //final tag w/ no comma after
        }
        return tagString;
    }
}
