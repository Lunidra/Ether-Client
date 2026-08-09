package luni.ether.core.network;

public class NetworkConfig {
    // Defaults to a local, unencrypted dev endpoint for local testing only.
    // Any real deployment MUST set ETHER_CHAT_SERVER to a wss:// URL —
    // plain ws:// sends chat/presence traffic (and previously would have
    // sent the account access token) unencrypted over the network.
    public static final String CHAT_SERVER = System.getenv().getOrDefault("ETHER_CHAT_SERVER", "ws://127.0.0.1:2832");

    public static final boolean IS_SECURE_TRANSPORT = CHAT_SERVER.startsWith("wss://");
}
