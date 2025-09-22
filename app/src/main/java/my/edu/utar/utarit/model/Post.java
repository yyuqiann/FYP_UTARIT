package my.edu.utar.utarit.model;

import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("username")
    private String username;

    @SerializedName("content")
    private String content;

    @SerializedName("likes")
    private int likes;

    @SerializedName("comments")
    private int comments;

    // Full constructor
    public Post(String userId, String username, String content, String id, int likes, int comments) {
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.id = id;
        this.likes = likes;
        this.comments = comments;
    }

    // Getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public int getComments() { return comments; }
    public void setComments(int comments) { this.comments = comments; }
}
