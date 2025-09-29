package my.edu.utar.utarit.network.model;

import com.google.gson.annotations.SerializedName;

public class SignInResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("expires_in")
    private int expiresIn;

    @SerializedName("token_type")
    private String tokenType;

    private User user;

    // Inner User class to match Supabase response
    public static class User {
        private String id;
        private String email;
        @SerializedName("email_confirmed_at")
        private String emailConfirmedAt;
        @SerializedName("created_at")
        private String createdAt;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getEmailConfirmedAt() { return emailConfirmedAt; }
        public void setEmailConfirmedAt(String emailConfirmedAt) { this.emailConfirmedAt = emailConfirmedAt; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }

    // Getters and Setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public int getExpiresIn() { return expiresIn; }
    public void setExpiresIn(int expiresIn) { this.expiresIn = expiresIn; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}