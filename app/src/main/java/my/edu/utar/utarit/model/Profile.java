package my.edu.utar.utarit.model;

import com.google.gson.annotations.SerializedName;

public class Profile {

    @SerializedName("id")
    private String id;

    @SerializedName("display_name")
    private String displayName;


    public String getId() { return id; }

    public String getDisplayName() { return displayName; }

}
