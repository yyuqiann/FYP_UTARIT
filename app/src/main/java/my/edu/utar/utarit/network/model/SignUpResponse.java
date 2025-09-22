package my.edu.utar.utarit.network.model;

public class SignUpResponse {
    private String access_token;
    private String refresh_token;
    private String token_type;
    private int expires_in;
    private User user;

    public String getAccessToken() { return access_token; }
    public String getRefreshToken() { return refresh_token; }
    public String getTokenType() { return token_type; }
    public int getExpiresIn() { return expires_in; }
    public User getUser() { return user; }

    public static class User {
        private String id;
        private String email;

        public String getId() { return id; }
        public String getEmail() { return email; }
    }
}
