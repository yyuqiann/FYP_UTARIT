package my.edu.utar.utarit.model;

import com.google.gson.annotations.SerializedName;

public class PostLike {
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("post_id")
    private String postId;

    // Getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
}
