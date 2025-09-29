package my.edu.utar.utarit.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class Comment {
    private String id;
    private String content;
    private String created_at;
    private String user_id;
    private Map<String, Object> users; // nested user object

    // Nested user class
    public static class Users {
        public String username;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedAt() { return created_at; }
    public void setCreatedAt(String created_at) { this.created_at = created_at; }

    public String getUserId() { return user_id; }
    public void setUserId(String user_id) { this.user_id = user_id; }

    // Return username from nested user object
    public String getUsername() {
        if (users != null && users.get("username") != null) {
            return users.get("username").toString();
        }
        return "Unknown";
    }

    // Format timestamp
    public String getFormattedTime() {
        if (created_at == null) return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(created_at);
            SimpleDateFormat output = new SimpleDateFormat("dd/MM/yy HH:mm");
            return output.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return created_at;
        }
    }
}
