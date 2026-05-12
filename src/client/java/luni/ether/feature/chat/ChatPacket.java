package luni.ether.feature.chat;

public class ChatPacket {

    public String type = "chat";
    public ChatMessage data;
    public String sessionToken;

    public ChatPacket(ChatMessage data) {
        this.data = data;
    }
}
