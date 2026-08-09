package luni.ether.feature.chat;

/**
 * First step of the auth handshake. Identifies the player to the backend
 * WITHOUT sending any Mojang/Xbox credential. The backend should respond
 * with an AuthChallengePacket ("auth_challenge") containing a random
 * serverId, which the client proves ownership against via Mojang's
 * session-join flow (see ChatClient#handleAuthChallenge).
 */
public class AuthPacket {

    public String type = "auth_hello";

    public String uuid;
    public String username;

    public AuthPacket(
            String uuid,
            String username
    ) {
        this.uuid = uuid;
        this.username = username;
    }
}
