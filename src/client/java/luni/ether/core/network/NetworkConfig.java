package luni.ether.core.network;

public class NetworkConfig {
    public static final String CHAT_SERVER = System.getenv().getOrDefault("ETHER_CHAT_SERVER", "ws://127.0.0.1:2832");
    public static final String CHAT_AUTH = System.getenv().getOrDefault("ETHER_CHAT_AUTH", "dev-token");
}
