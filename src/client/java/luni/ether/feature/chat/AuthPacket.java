package luni.ether.feature.chat;

public class AuthPacket {

    public String type = "auth";

    public String uuid;
    public String username;

    public String accessToken;

    public AuthPacket(
            String uuid,
            String username,
            String accessToken
    ) {
        this.uuid = uuid;
        this.username = username;
        this.accessToken = accessToken;
    }
}
