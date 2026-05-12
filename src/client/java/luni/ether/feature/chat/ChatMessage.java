package luni.ether.feature.chat;

public class ChatMessage {

    public String uuid;
    public String username;
    public String message;
    public String server;
    public String serverIP;

    public boolean isStaff;
    public boolean isDeveloper;
    public boolean isBetaTester;

    public ChatMessage(String uuid, String username, String message, String server, String serverIP) {
        this.uuid = uuid;
        this.username = username.replaceAll("§.", "");
        this.message = message;
        this.server = server.replaceAll("\\u00A7.", ""); // strip formatting;
        this.serverIP = serverIP;
    }
}
