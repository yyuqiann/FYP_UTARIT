package my.edu.utar.utarit.model;

public class Message {
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private String timestamp;

    public Message() {}

    public Message(String senderId, String receiverId, String content, String timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }

    public void setId(String id) { this.id = id; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
