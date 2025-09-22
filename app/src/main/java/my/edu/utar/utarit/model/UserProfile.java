package my.edu.utar.utarit.model;

import com.google.gson.annotations.SerializedName;

public class UserProfile {

    private String id;
    private String username;
    private String email;
    private String bio;

    @SerializedName("profile_url")
    private String profileUrl;

    @SerializedName("banner_url")
    private String bannerUrl;

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfileUrl() { return profileUrl; }
    public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }

    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
}
