package my.edu.utar.utarit.model;

import com.google.gson.annotations.SerializedName;

public class PostCreateRequest {

    @SerializedName("user_id")
    private String userId;

    private String content;

    public PostCreateRequest(String userId, String content) {
        this.userId = userId;
        this.content = content;
    }

    // Optional: getters if needed
    public String getUserId() { return userId; }
    public String getContent() { return content; }
}
