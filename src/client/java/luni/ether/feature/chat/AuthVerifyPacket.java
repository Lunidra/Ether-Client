package luni.ether.feature.chat;

/**
 * Sent after the client has successfully called Mojang's session-join
 * endpoint with the serverId supplied in an AuthChallengePacket. The
 * backend must independently confirm this by calling Mojang's
 * `hasJoined` endpoint with the same username + serverId before trusting
 * the connection as authenticated. The client never transmits its
 * access token to the backend at any point in this flow.
 */
public class AuthVerifyPacket {

    public String type = "auth_verify";

    public String serverId;

    public AuthVerifyPacket(String serverId) {
        this.serverId = serverId;
    }
}
