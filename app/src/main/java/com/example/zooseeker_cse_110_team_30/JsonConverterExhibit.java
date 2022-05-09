package com.example.zooseeker_cse_110_team_30;

import java.util.List;

public class JsonConverterExhibit {
    public String id;     //internal identifier
    public String kind;         //type of location
    public String name;         //external identifier
    public List<String> tags;         //object tags

    public JsonConverterExhibit(String id, String kind, String name, List<String> tags) {
        this.id = id;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
    }

    public String getTagString() {
        String tagString = "";
        if(tags.size() > 0) {
            for (int i = 0; i < tags.size() - 1; i++) {
                tagString = tagString + tags.get(i) + ",";
            }
            tagString = tagString + tags.get(tags.size() - 1);
        }
        return tagString;
    }
}
