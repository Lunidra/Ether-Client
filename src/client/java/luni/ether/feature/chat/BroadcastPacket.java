package luni.ether.feature.chat;

public class BroadcastPacket {

    public String type = "broadcast";
    public String message;
    public String sessionToken;

    public BroadcastPacket(String message) {
        this.message = message;
    }
}
