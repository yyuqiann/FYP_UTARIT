package my.edu.utar.utarit.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "utarit_session";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";  // add email

    private static SessionManager instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Singleton instance
    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    // Save session data (after login or registration)
    public void saveSession(String accessToken, String userId, String username, String email) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);  // save email
        editor.apply();
    }

    // Get access token
    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    // Get current user ID
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    // Get current username
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "Unknown");
    }

    public void setUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    // Get current email
    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "Unknown");
    }

    public void setEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    // Clear session (logout)
    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return getAccessToken() != null && getUserId() != null;
    }
}
