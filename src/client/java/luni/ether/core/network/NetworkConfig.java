package luni.ether.core.network;

public class NetworkConfig {
    // Defaults to a local, unencrypted dev endpoint for local testing only.
    // Any real deployment MUST set ETHER_CHAT_SERVER to a wss:// URL —
    // plain ws:// sends chat/presence traffic (and previously would have
    // sent the account access token) unencrypted over the network.
    //

    //TODO: localhost
    public static final String CHAT_SERVER = System.getenv().getOrDefault("ETHER_CHAT_SERVER", "ws://localhost:2832/ws");


    //TODO: Public
    //public static final String CHAT_SERVER = System.getenv().getOrDefault("ETHER_CHAT_SERVER", "wss://eapi.noxio.uk/ws");


    public static final boolean IS_SECURE_TRANSPORT = CHAT_SERVER.startsWith("wss://");

    public static final boolean DEV_AUTH = true;
}
