package my.edu.utar.utarit.model;

public class Chat {
    private String chatId;
    private String username;
    private String lastMessage;

    public Chat() {}

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
}
