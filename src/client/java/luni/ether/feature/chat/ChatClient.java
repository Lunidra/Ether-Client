package luni.ether.feature.chat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neovisionaries.ws.client.*;

import luni.ether.core.network.NetworkConfig;
import luni.ether.feature.module.mods.QoL.ChatEnhancer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatClient {

    private static ChatClient INSTANCE;
    private WebSocket socket;

    public static ChatClient get() {
        if (INSTANCE == null) INSTANCE = new ChatClient();
        return INSTANCE;
    }

    private boolean shouldReconnect = true;

    private boolean connected = false;
    private boolean authenticated = false;



    private Minecraft mc = Minecraft.getInstance();

    private String sessionToken;

    public String getSessionToken() {
        return sessionToken;
    }

    private String lastUUID = getUUID();

    private final Map<String, String> etherUsers = new HashMap<>();

    public boolean isEtherUser(UUID uuid) {
        if (mc.player != null && uuid.equals(mc.player.getUUID())) {
            return true; // always include self
        }

        return etherUsers.containsKey(uuid.toString()) || authenticated;
    }

    public String getUserServer(UUID uuid) {
        return etherUsers.get(uuid.toString());
    }



    private String getUUID() {
        return mc.getUser().getProfileId().toString();
    }

    private String getUsername() {
        return mc.getUser().getName();
    }
    String AuthToken = UUID.randomUUID().toString();

    public boolean isConnected() {
        return connected;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }



    public void connect() {
        if (socket != null && socket.isOpen()) return;

        if (socket != null) {
            return; // connection already in progress
        }

        try {
            WebSocketFactory factory = new WebSocketFactory();

            socket = factory
                    .createSocket(NetworkConfig.CHAT_SERVER) // later → wss://
                    .addHeader("Authorization", "Bearer " + NetworkConfig.CHAT_AUTH) // auth ready
                    .addListener(new WebSocketAdapter() {



                        @Override
                        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                            connected = true;
                            authenticated = false;

                            //String accessToken = mc.getUser().getAccessToken();
                            AuthPacket auth = new AuthPacket(getUUID(), getUsername(), AuthToken);
                            String json = new Gson().toJson(auth);
                            send(json);
                            notifyUser("Connected to EtherChat Backend!");
                            System.out.println("[EtherChat] Connected");
                        }

                        @Override
                        public void onTextMessage(WebSocket websocket, String message) {
                            System.out.println("[EtherChat] RECEIVED: " + message);
                            handleIncoming(message);
                            //Minecraft.getInstance().gui.getChat().addMessage(Component.literal(message));
                        }

                        @Override
                        public void onDisconnected(WebSocket websocket,
                                                   WebSocketFrame serverCloseFrame,
                                                   WebSocketFrame clientCloseFrame,
                                                   boolean closedByServer) {

                            System.out.println("[EtherChat] Disconnected");
                            notifyUser("Not connected to EtherChat");

                            connected = false;
                            authenticated = false;
                            sessionToken = null;
                            socket = null;

                            if (shouldReconnect) {
                                new Thread(() -> {
                                    try {
                                        Thread.sleep(3000); // wait 3s
                                        System.out.println("[EtherChat] Reconnecting...");
                                        connect();
                                    } catch (Exception ignored) {}
                                }).start();
                            }
                        }

                        @Override
                        public void onError(WebSocket websocket, WebSocketException cause) {

                            cause.printStackTrace();
                        }
                    });

            socket.connectAsynchronously(); // ✅ non-blocking (important for MC)

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void send(String message) {
//        if (socket != null && socket.isOpen()) {
//            socket.sendText(message);
//        }
//    }

    public boolean send(String message) {
        if (socket == null || !socket.isOpen()) {
            notifyUser("Not connected");
            return false;
        } else {
            socket.sendText(message);
            return true;
        }
    }

    private final Gson gson = new Gson();
    private void handleIncoming(String msg) {
        Minecraft mc = Minecraft.getInstance();

        mc.execute(() -> {
            try {
                JsonObject obj = JsonParser.parseString(msg).getAsJsonObject();

                if (obj.has("type") && obj.get("type").getAsString().equals("auth_success")) {
                    sessionToken = obj.get("token").getAsString();
                    System.out.println("[EtherChat] Authenticated With Token: " + sessionToken);
                    authenticated = true;

                    // request fresh snapshot
                    send("{\"type\":\"presence_request\"}");

                    return;
                }

                if (obj.has("type") && obj.get("type").getAsString().equals("presence_sync")) {

                    etherUsers.clear();

                    var arr = obj.getAsJsonArray("users");

                    for (var el : arr) {
                        var u = el.getAsJsonObject();

                        String rawUUID = u.get("uuid").getAsString();

                        // normalize BOTH sides to same format
                        String uuid = formatUUID(rawUUID);


                        String server = u.get("server").getAsString();

                        etherUsers.put(uuid, server);

                        System.out.println("[ETHER PRESENCE] " + etherUsers.size() + " users");
                    }

                    return;
                }


                // user join
                if (obj.get("type").getAsString().equals("user_join")) {
                    String uuid = formatUUID(obj.get("uuid").getAsString());

                    etherUsers.put(uuid, "Unknown"); // no server yet

                    System.out.println("[ETHER JOIN] " + uuid);
                    return;
                }

                // user leave
                if (obj.has("type") && obj.get("type").getAsString().equals("user_leave")) {
                    String uuid = formatUUID(obj.get("uuid").getAsString());

                    etherUsers.remove(uuid);

                    System.out.println("[ETHER LEAVE] " + uuid);
                    return;
                }

                if (obj.has("type") && obj.get("type").getAsString().equals("user_update")) {
                    String uuid = formatUUID(obj.get("uuid").getAsString());
                    String server = obj.get("server").getAsString();

                    etherUsers.put(uuid, server);

                    System.out.println("[ETHER UPDATE] " + uuid + " -> " + server);
                    return;
                }






                if (obj.has("type") && obj.get("type").getAsString().equals("broadcast")) {
                    String message = obj.get("message").getAsString();
                    String sender = obj.get("sender").getAsString();

                    String formatted = String.format(
                            "§6§l[ETHER BROADCAST] §r§e%s§7: §f%s",
                            sender,
                            message
                    );

                    if (mc.player != null) {
                        mc.player.sendSystemMessage(Component.literal(formatted));
                        mc.player.playSound(net.minecraft.sounds.SoundEvents.NOTE_BLOCK_PLING.value(), 0.8f, 1.5f);
                    }

                    return;
                }






                ChatMessage packet = gson.fromJson(msg, ChatMessage.class);





//                if (packet.uuid != null && packet.uuid.equals(localUUID)) {
//                    return; // ✅ skip ONLY your own messages
//                }
                boolean isSelf = packet.uuid != null && packet.uuid.equals(getUUID());

                //String cleanMsg = packet.message.replace("[self]", "");

                String prefix = "";
                String nameColor = "§f";

                if (packet.isDeveloper) {
                    prefix = "§7[§dDEV§7] ";
                    nameColor = "§d";
                } else if (packet.isStaff) {
                    prefix = "§7[§bSTAFF§7] ";
                    nameColor = "§b";
                } else if (packet.isBetaTester) {
                    prefix = "§o§7[§4BETA TESTER§7] ";
                    nameColor = "§c";
                }

//                String formatted = String.format(
//                        "§7[§bEtherChat§7] §7(%s) %s%s%s§7: §f%s",
//                        packet.server,
//                        prefix,
//                        nameColor,
//                        packet.username,
//                        packet.message
//                );

                //TODO MODULE ADDITIONS
                boolean mention = false;

                if (ChatEnhancer.INSTANCE != null && ChatEnhancer.INSTANCE.isEnabled() && ChatEnhancer.INSTANCE.highlightMentions.get()) {
                    String msgLower = packet.message.toLowerCase();
                    String userLower = getUsername().toLowerCase();

                    mention = msgLower.contains(userLower);
                }

                if (ChatEnhancer.INSTANCE != null && ChatEnhancer.INSTANCE.isEnabled() && ChatEnhancer.INSTANCE.highlightSelf.get() && isSelf) {
                    nameColor = "§a§l";
                }

                //TODO MODULE ADDITIONS
                String timePrefix = "";

                if (ChatEnhancer.INSTANCE != null && ChatEnhancer.INSTANCE.isEnabled() && ChatEnhancer.INSTANCE.timestamps.get()) {
                    String time = java.time.LocalTime.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

                    timePrefix = "§8[" + time + "] ";
                }


                String formatted = String.format(
                        "%s§7[§bEtherChat§7] §7(%s) %s%s%s§7: §f%s",
                        timePrefix,
                        packet.server,
                        prefix,
                        nameColor,
                        packet.username,
                        packet.message
                );

                if (mention) {
                    formatted = "§e" + formatted;

                    if (mc.player != null) {
                        mc.player.playSound(
                                net.minecraft.sounds.SoundEvents.NOTE_BLOCK_PLING.value(),
                                0.7f,
                                1.5f
                        );
                    }
                }

                Component text = Component.literal(formatted).withStyle(style -> style.withHoverEvent(
                        new HoverEvent.ShowText(
                                Component.literal("§7Server IP: §f" + packet.serverIP)
                        )
                ));

                if (mc.player != null) {
                    mc.player.sendSystemMessage(text);
                }

            } catch (Exception e) {
                System.out.println("[EtherChat] JSON ERROR: " + msg);
            }
        });
    }

    public void tick() {
        String currentUUID = getUUID();

        if (!currentUUID.equals(lastUUID)) {
            System.out.println("[EtherChat] Account changed → reconnecting");

            lastUUID = currentUUID;

            if (socket != null) {
                shouldReconnect = false;
                socket.disconnect();
            }

            shouldReconnect = true;
            connect();
        }
    }

    public void notifyUser(String msg) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null) {
            mc.player.sendSystemMessage(
                    Component.literal("§7[§bEtherChat§7] §6" + msg));
        }
    }

    private String formatUUID(String uuid) {
        return uuid.replaceFirst(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                "$1-$2-$3-$4-$5"
        );
    }

    public void shutdown() {

        shouldReconnect = false;

        connected = false;
        authenticated = false;

        try {

            if (socket != null) {
                socket.disconnect();
            }

        } catch (Exception ignored) {}

        socket = null;
    }
}