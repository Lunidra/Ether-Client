package luni.ether.feature.chat;

public class AuthPacket {

    public String type = "auth";
    public String accessToken;

    public String uuid;
    public String username;
    public String token;

    public AuthPacket(String uuid, String username, String token) {
        this.uuid = uuid;
        this.username = username;
        this.token = token;
    }
}
