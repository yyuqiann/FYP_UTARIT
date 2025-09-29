package my.edu.utar.utarit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {

    private static final String PREF_NAME = "utarit_session";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_LAST_LOGIN = "last_login"; // Timestamp for last login

    private static SessionManager instance;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

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

    // Save session info
    public void saveSession(String accessToken, String userId, String username, String email) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putLong(KEY_LAST_LOGIN, System.currentTimeMillis());  // Track last login time
        editor.apply();
        Log.d("SessionManager", "Session saved for user: " + username);
    }

    // Get the stored access token
    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    // Get the stored user ID
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    // Set the username in the session
    public void setUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
        Log.d("SessionManager", "Username updated: " + username);
    }

    // Get the stored username
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "Unknown");
    }

    // Get the stored email
    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "Unknown");
    }

    // Set the email in the session
    public void setEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.apply();
        Log.d("SessionManager", "Email updated: " + email);
    }

    // Clear the session
    public void clearSession() {
        editor.clear().apply();
        Log.d("SessionManager", "Session cleared.");
    }

    // Check if the user is logged in (also checking session expiration)
    public boolean isLoggedIn() {
        long lastLogin = sharedPreferences.getLong(KEY_LAST_LOGIN, 0);
        long currentTime = System.currentTimeMillis();
        long sessionDuration = currentTime - lastLogin;

        // Example: expire the session if 30 minutes have passed
        if (sessionDuration > 30 * 60 * 1000) {
            clearSession();  // Clear session if expired
            return false;
        }

        return getAccessToken() != null && getUserId() != null;
    }
}
