package my.edu.utar.utarit.model;

import com.google.gson.annotations.SerializedName;

public class Profile {
    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("username")
    private String username;

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}
