package luni.ether.mixin;

import com.google.gson.Gson;
import luni.ether.core.EtherClient;
import luni.ether.feature.chat.BroadcastPacket;
import luni.ether.feature.chat.ChatClient;
import luni.ether.feature.chat.ChatMessage;
import luni.ether.feature.chat.ChatPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {



    @Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
    private void ether$handleChat(String message, CallbackInfo ci) {

        System.out.println("[Ether] Chat intercepted: " + message);

        if (message.startsWith("``") || message.length() <= 1) {
            ci.cancel();

            String raw = message.substring(2).trim();

            if (raw.isEmpty()) return;

            ChatClient chat = ChatClient.get();

            if (!chat.isConnected()) {
                chat.notifyUser("Not connected to EtherChat");
                return;
            }

            if (!chat.isAuthenticated()) {
                chat.notifyUser("Authenticating...");
                return;
            }

            // Optional: enforce command format
            String lower = raw.toLowerCase();

            String content;

            // support both broadcast + bc
            if (lower.startsWith("broadcast ")) {
                content = raw.substring("broadcast ".length());

            } else if (lower.startsWith("bc ")) {
                content = raw.substring("bc ".length());

            } else {
                chat.notifyUser("Usage: ``broadcast/``bc <message>");
                return;
            }

            content = content.trim();
            if (content.isEmpty()) return;

            BroadcastPacket packet = new BroadcastPacket(content);
            packet.sessionToken = chat.getSessionToken();

            chat.send(new Gson().toJson(packet));
            return;
        }


        if (!message.startsWith("!") || message.length() <= 1) {
            return;
        }

        ci.cancel();

        String clean = message.substring(1).trim();

        if (clean.isEmpty()) {
            return;
        }

        ChatClient chat = ChatClient.get();

        if (!chat.isConnected()) {
            chat.notifyUser("Not connected to EtherChat");
            return;
        }

        if (!chat.isAuthenticated()) {
            chat.notifyUser("Authenticating...");
            return;
        }



        Minecraft mc = Minecraft.getInstance();

        String username = mc.getUser().getName();
        String uuid = mc.getUser().getProfileId().toString();

        String serverName = "Unknown";
        String serverIp = "Unknown";

        if (mc.isSingleplayer()) {
            serverName = "Singleplayer";
            serverIp = "localhost";

        } else {
            // ALWAYS try connection FIRST (real data)
            if (mc.getConnection() != null && mc.getConnection().getConnection() != null) {

                String address = mc.getConnection().getConnection().getRemoteAddress().toString();

                if (address.contains("/")) {
                    serverIp = address.split("/")[0];
                } else {
                    serverIp = address;
                }

                if (serverIp.contains(":")) {
                    serverIp = serverIp.split(":")[0];
                }

            }

            // fallback name
            if (mc.getCurrentServer() != null) {
                serverName = mc.getCurrentServer().name != null
                        ? mc.getCurrentServer().name
                        : "Server";
            } else {
                serverName = "Server";
            }
        }






        ChatMessage msg = new ChatMessage(uuid, username, clean, serverName, serverIp);
        msg.gameVersion = EtherClient.MC_VER;


        msg.message = clean;
        // convert to JSON
        String json = new Gson().toJson(msg);

        ChatPacket packet = new ChatPacket(msg);
        packet.sessionToken = ChatClient.get().getSessionToken();
        String wrapped = new Gson().toJson(packet);

        if (ChatClient.get().getSessionToken() == null) {
            System.out.println("[EtherChat] Not authenticated yet");
            return;
        }

        System.out.println("SENDING: " + wrapped); // DEBUG

        ChatClient.get().send(wrapped);







//        ci.cancel();
    }
}

