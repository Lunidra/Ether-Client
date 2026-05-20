package luni.ether.feature.chat;

public class EtherUser {

    public String uuid;
    public String username;

    public String server;
    public String gameVersion;

    public boolean developer;
    public boolean staff;
    public boolean beta;

    public EtherUser(
            String uuid,
            String username
    ) {
        this.uuid = uuid;
        this.username = username;
    }
}