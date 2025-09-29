package my.edu.utar.utarit.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Post {
    private String id;
    private String content;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("created_at")
    private String createdAt;

    // Relation: posts.user_id -> auth.users.id
    @SerializedName("users")
    private User user;

    @SerializedName("post_likes")
    private List<PostLike> postLikes;

    @SerializedName("post_comments")
    private List<Comment> postComments;

    // ========= Inner classes =========

    // Nested user object from auth.users
    public static class User {
        private String id;
        private String email;

        @SerializedName("raw_user_meta_data")
        private MetaData rawUserMetaData;

        public static class MetaData {
            private String username;

            public String getUsername() { return username; }
            public void setUsername(String username) { this.username = username; }
        }

        public String getId() { return id; }
        public String getEmail() { return email; }
        public MetaData getRawUserMetaData() { return rawUserMetaData; }
    }

    // Inner class for PostLike
    public static class PostLike {
        private String id;

        @SerializedName("user_id")
        private String userId;

        @SerializedName("post_id")
        private String postId;

        @SerializedName("created_at")
        private String createdAt;

        // Getters & setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getPostId() { return postId; }
        public void setPostId(String postId) { this.postId = postId; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }

    // ========= Getters & setters =========
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<PostLike> getPostLikes() { return postLikes; }
    public void setPostLikes(List<PostLike> postLikes) { this.postLikes = postLikes; }

    public List<Comment> getPostComments() { return postComments; }
    public void setPostComments(List<Comment> postComments) { this.postComments = postComments; }

    // ========= Utility methods =========
    public int getLikesCount() {
        return postLikes != null ? postLikes.size() : 0;
    }

    public int getCommentsCount() {
        return postComments != null ? postComments.size() : 0;
    }

    public boolean isLikedBy(String userId) {
        if (postLikes == null || userId == null) return false;
        for (PostLike like : postLikes) {
            if (userId.equals(like.getUserId())) return true;
        }
        return false;
    }

    // Helper: prefer username, fallback to email
    public String getDisplayName() {
        if (user != null) {
            if (user.getRawUserMetaData() != null && user.getRawUserMetaData().getUsername() != null) {
                return user.getRawUserMetaData().getUsername();
            }
            return user.getEmail();
        }
        return "Unknown User";
    }

    public void addLike(String userId) {
        if (postLikes == null) postLikes = new ArrayList<>();
        PostLike like = new PostLike();
        like.setUserId(userId);
        like.setPostId(this.id);
        postLikes.add(like);
    }

    public void removeLike(String userId) {
        if (postLikes == null) return;
        for (int i = 0; i < postLikes.size(); i++) {
            if (userId.equals(postLikes.get(i).getUserId())) {
                postLikes.remove(i);
                break;
            }
        }
    }

    // ========= Comment helper =========
    public void addCommentCount(int increment) {
        if (postComments == null) postComments = new ArrayList<>();
        // Optionally, just increment the size virtually (no real object added)
        // But if you want, you can add a dummy Comment placeholder
        for (int i = 0; i < increment; i++) {
            postComments.add(new Comment()); // dummy comment to update count
        }
    }

}
