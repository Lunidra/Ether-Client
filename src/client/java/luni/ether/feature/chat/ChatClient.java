package luni.ether.feature.chat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neovisionaries.ws.client.*;

import luni.ether.core.DevFlags;
import luni.ether.core.EtherClient;
import luni.ether.core.network.NetworkConfig;
import luni.ether.feature.module.mods.QoL.ChatEnhancer;
import luni.ether.ui.notification.NotificationManager;
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
    private String lastPresenceServer = "";
    private String lastPresenceIP = "";


    //    private final Map<String, String> etherUsers = new HashMap<>();
    private final Map<String, EtherUser> etherUsers = new HashMap<>();

    public boolean isEtherUser(UUID uuid) {
        if (mc.player != null && uuid.equals(mc.player.getUUID())) {
            return true; // always include self
        }

        //return etherUsers.containsKey(uuid.toString()) || authenticated;
        return etherUsers.containsKey(uuid.toString())
                || authenticated
                || DevFlags.DEV_MODE;
    }

    public String getUserServer(UUID uuid) {

        EtherUser user =
                etherUsers.get(uuid.toString());

        return user != null
                ? user.server
                : "Unknown";
    }



    private String getUUID() {
        return mc.getUser().getProfileId().toString();
    }

    private String getUsername() {
        return mc.getUser().getName();
    }
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

            if (!NetworkConfig.IS_SECURE_TRANSPORT) {
                EtherClient.LOGGER.warn(
                        "[EtherChat] Connecting over an insecure ws:// endpoint ({}). " +
                                "Use wss:// for anything other than local testing.",
                        NetworkConfig.CHAT_SERVER
                );
            }

            socket = factory
                    .createSocket(NetworkConfig.CHAT_SERVER)
                    .addListener(new WebSocketAdapter() {



                        @Override
                        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                            connected = true;
                            authenticated = false;

                            // Identify only — no access token is sent. The backend must
                            // challenge us (auth_challenge) and we prove ownership via
                            // Mojang's session-join flow. See handleAuthChallenge().
                            AuthPacket auth = new AuthPacket(getUUID(), getUsername());
                            String json = new Gson().toJson(auth);
                            send(json);
                            notifyUser("Connected to EtherChat Backend!");
                            //System.out.println("[EtherChat] Connected");
                        }

                        @Override
                        public void onTextMessage(WebSocket websocket, String message) {
                            //System.out.println("[EtherChat] RECEIVED: " + message);
                            handleIncoming(message);
                            //Minecraft.getInstance().gui.getChat().addMessage(Component.literal(message));
                        }

                        @Override
                        public void onDisconnected(WebSocket websocket,
                                                   WebSocketFrame serverCloseFrame,
                                                   WebSocketFrame clientCloseFrame,
                                                   boolean closedByServer) {

                            //System.out.println("[EtherChat] Disconnected");
                            notifyUser("Not connected to EtherChat");

                            connected = false;
                            authenticated = false;
                            sessionToken = null;
                            socket = null;

                            if (shouldReconnect) {
                                new Thread(() -> {
                                    try {
                                        Thread.sleep(3000); // wait 3s
                                        //System.out.println("[EtherChat] Reconnecting...");
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

                if (obj.has("type") && obj.get("type").getAsString().equals("auth_challenge")) {
                    String serverId = obj.get("serverId").getAsString();
                    handleAuthChallenge(serverId);
                    return;
                }

                if (obj.has("type") && obj.get("type").getAsString().equals("auth_success")) {
                    sessionToken = obj.get("token").getAsString();
                    //                System.out.println("[EtherChat] Authenticated With Token: " + sessionToken);
                    authenticated = true;

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


                        EtherUser user = new EtherUser(
                                uuid,
                                u.get("username").getAsString()
                        );

                        user.server =
                                u.get("server").getAsString();

                        if (u.has("gameVersion")) {
                            user.gameVersion =
                                    u.get("gameVersion").getAsString();
                        }

                        etherUsers.put(uuid, user);

                        //System.out.println("[ETHER PRESENCE] " + etherUsers.size() + " users");

                    }

                    return;
                }


                // user join
//                if (obj.has("type") && obj.get("type").getAsString().equals("user_join")) {
//                    String uuid = formatUUID(obj.get("uuid").getAsString());
//
//                    EtherUser user =
//                            new EtherUser(uuid, "Unknown");
//
//                    user.server = "Unknown";
//
//                    etherUsers.put(uuid, user);
//
//                    //System.out.println("[ETHER JOIN] " + uuid);
//                    return;
//                }


                // user join
                if (obj.has("type") && obj.get("type").getAsString().equals("user_join")) {

                    String uuid = formatUUID(
                            obj.get("uuid").getAsString()
                    );

                    String username = obj.has("username")
                            ? obj.get("username").getAsString()
                            : "Unknown";

                    String server = obj.has("server")
                            ? obj.get("server").getAsString()
                            : "Unknown";

                    String gameVersion = obj.has("gameVersion")
                            ? obj.get("gameVersion").getAsString()
                            : "Unknown";

                    EtherUser user = etherUsers.get(uuid);

                    if (user == null) {
                        user = new EtherUser(
                                uuid,
                                username
                        );

                        etherUsers.put(uuid, user);
                    } else if (!username.equals("Unknown")) {
                        user.username = username;
                    }

                    if (!server.equals("Unknown")) {
                        user.server = server;
                    }

                    if (!gameVersion.equals("Unknown")) {
                        user.gameVersion = gameVersion;
                    }

                    return;
                }

                // user leave
                if (obj.has("type") && obj.get("type").getAsString().equals("user_leave")) {
                    String uuid = formatUUID(obj.get("uuid").getAsString());

                    etherUsers.remove(uuid);

                    //System.out.println("[ETHER LEAVE] " + uuid);
                    return;
                }

//                if (obj.has("type") && obj.get("type").getAsString().equals("user_update")) {
//
//                    String uuid =
//                            formatUUID(
//                                    obj.get("uuid").getAsString()
//                            );
//
//                    String server =
//                            obj.get("server").getAsString();
//
//                    EtherUser user =
//                            etherUsers.get(uuid);
//
//                    if (user != null) {
//
//                        user.server = server;
//
//                        if (obj.has("gameVersion")) {
//                            user.gameVersion =
//                                    obj.get("gameVersion").getAsString();
//                        }
//                    }
//
//                    //System.out.println("[ETHER UPDATE] " + uuid + " -> " + server);
//
//                    return;
//                }

                if (obj.has("type") && obj.get("type").getAsString().equals("user_update")) {

                    String uuid = formatUUID(
                            obj.get("uuid").getAsString()
                    );

                    EtherUser user = etherUsers.get(uuid);

                    if (user == null) {
                        user = new EtherUser(
                                uuid,
                                obj.has("username")
                                        ? obj.get("username").getAsString()
                                        : "Unknown"
                        );

                        etherUsers.put(uuid, user);
                    }

                    if (obj.has("username")) {
                        String username = obj.get("username").getAsString();

                        if (!username.isEmpty()) {
                            user.username = username;
                        }
                    }

                    if (obj.has("server")) {
                        user.server =
                                obj.get("server").getAsString();
                    }

                    if (obj.has("gameVersion")) {
                        user.gameVersion =
                                obj.get("gameVersion").getAsString();
                    }

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
                        mc.player.displayClientMessage(Component.literal(formatted), false);
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


                String versionInfo = "";

                if (ChatEnhancer.INSTANCE != null
                        && ChatEnhancer.INSTANCE.isEnabled()
                        && ChatEnhancer.INSTANCE.Versions.get()
                        && packet.gameVersion != null) {

                    versionInfo = packet.gameVersion + " • ";
                }


                String formatted = String.format(
                        "%s§7[§bEtherChat§7] §7(%s) %s%s%s§7: §f%s",
                        timePrefix,
                        versionInfo + packet.server,
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
                    mc.player.displayClientMessage(text, false
//                            net.minecraft.network.chat.Component.literal(formatted),
//                            false // THIS puts it in chat



                    );
                }

            } catch (Exception e) {
                System.out.println("[EtherChat] JSON ERROR: " + msg);
            }
        });
    }

    public void tick() {
        String currentUUID = getUUID();

        if (!currentUUID.equals(lastUUID)) {
            //System.out.println("[EtherChat] Account changed → reconnecting");

            lastUUID = currentUUID;

            if (socket != null) {
                shouldReconnect = false;
                socket.disconnect();
            }

            shouldReconnect = true;
            connect();
        }

        if (authenticated && connected) {



            String serverName = "Unknown";
            String serverIp = "Unknown";

            if (mc.isSingleplayer()) {

                serverName = "Singleplayer";
                serverIp = "localhost";

            } else {

                if (mc.getConnection() != null
                        && mc.getConnection().getConnection() != null) {

                    String address =
                            mc.getConnection()
                                    .getConnection()
                                    .getRemoteAddress()
                                    .toString();

                    if (address.contains("/")) {
                        serverIp = address.split("/")[0];
                    } else {
                        serverIp = address;
                    }

                    if (serverIp.contains(":")) {
                        serverIp = serverIp.split(":")[0];
                    }
                }

                if (mc.getCurrentServer() != null) {

                    serverName =
                            mc.getCurrentServer().name != null
                                    ? mc.getCurrentServer().name
                                    : "Server";
                }
            }

            boolean changed =
                    !serverName.equals(lastPresenceServer)
                            || !serverIp.equals(lastPresenceIP);

            if (changed) {

                lastPresenceServer = serverName;
                lastPresenceIP = serverIp;

                JsonObject presence = new JsonObject();

                presence.addProperty("type", "presence");
                presence.addProperty("server", serverName);
                presence.addProperty("serverIP", serverIp);
                presence.addProperty(
                        "gameVersion",
                        EtherClient.MC_VER
                );

                send(presence.toString());

                //System.out.println("[ETHER PRESENCE] Updated -> " + serverName);
            }
        }
    }

    /**
     * Proves ownership of the account to the backend using Mojang's own
     * session-join mechanism (the same check a vanilla server does),
     * instead of handing over our access token to a third party.
     * <p>
     * 1. Backend sends us a random serverId (auth_challenge).
     * 2. We call Mojang's session service joinServer(profile, accessToken, serverId)
     *    directly from the client — the access token is used locally and never
     *    leaves the device.
     * 3. We tell the backend we're done (auth_verify + serverId).
     * 4. The backend independently calls Mojang's hasJoined endpoint with our
     *    username + serverId to confirm the join actually happened before
     *    marking the connection authenticated (auth_success).
     * <p>
     * This runs the actual network call off the render thread since it's a
     * blocking HTTP request to Mojang.
     */
//    private void handleAuthChallenge(String serverId) {
//        new Thread(() -> {
//            try {
//                // Minecraft's session service field isn't consistently exposed via a
//                // public getter across versions, but its Mojang-mapped field name is
//                // stable ("minecraftSessionService"), so we pull it directly rather
//                // than guess at a getter method name.
//                java.lang.reflect.Field field = Minecraft.class.getDeclaredField("minecraftSessionService");
//                field.setAccessible(true);
//                com.mojang.authlib.minecraft.MinecraftSessionService sessionService =
//                        (com.mojang.authlib.minecraft.MinecraftSessionService) field.get(mc);
//
//                java.util.UUID profileId = mc.getUser().getProfileId();
//
//                sessionService.joinServer(profileId, mc.getUser().getAccessToken(), serverId);
//
//                send(new Gson().toJson(new AuthVerifyPacket(serverId)));
//
//            } catch (Exception e) {
//                // Session join failed (offline account, expired token, Mojang
//                // outage, etc.) — surface it, don't silently pretend we're
//                // authenticated.
//                mc.execute(() -> notifyUser("Failed to verify session with Mojang: " + e.getMessage()));
//            }
//        }, "Ether-SessionJoin").start();
//    }


    private void handleAuthChallenge(String serverId) {
        new Thread(() -> {
            try {
                EtherClient.LOGGER.info(
                        "[EtherChat] Received authentication challenge: {}",
                        serverId
                );

                if (DevFlags.DEV_MODE) {
                    EtherClient.LOGGER.info(
                            "[EtherChat] Development mode: skipping Mojang session join"
                    );

                    AuthVerifyPacket verify = new AuthVerifyPacket(serverId);
                    send(new Gson().toJson(verify));
                    return;
                }

                com.mojang.authlib.minecraft.MinecraftSessionService sessionService =
                        mc.services().sessionService();

                if (sessionService == null) {
                    throw new IllegalStateException(
                            "MinecraftSessionService is null"
                    );
                }

                java.util.UUID profileId =
                        mc.getUser().getProfileId();

                EtherClient.LOGGER.info(
                        "[EtherChat] Calling Minecraft session joinServer() for {}",
                        profileId
                );

                sessionService.joinServer(
                        profileId,
                        mc.getUser().getAccessToken(),
                        serverId
                );



                EtherClient.LOGGER.info(
                        "[EtherChat] Mojang session joinServer() succeeded"
                );

                AuthVerifyPacket verify =
                        new AuthVerifyPacket(serverId);

                String json =
                        new Gson().toJson(verify);

                EtherClient.LOGGER.info(
                        "[EtherChat] Sending auth_verify"
                );

                send(json);

            } catch (Exception e) {

                EtherClient.LOGGER.error(
                        "[EtherChat] Authentication challenge failed",
                        e
                );

                notifyUser(
                        "EtherChat authentication failed: "
                                + e.getClass().getSimpleName()
                );
            }
        }, "EtherChat-Auth").start();
    }



    public void notifyUser(String msg) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null) {
            mc.player.displayClientMessage(
                    Component.literal("§7[§bEtherChat§7] §6" + msg),
                    false
            );
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

    public Map<String, EtherUser> getEtherUsers() {
        return etherUsers;
    }
}