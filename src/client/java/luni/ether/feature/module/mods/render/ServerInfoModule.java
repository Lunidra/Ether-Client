package luni.ether.feature.module.mods.render;

import luni.ether.feature.module.Category;
import luni.ether.feature.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;

public class ServerInfoModule extends Module {

    private final Minecraft mc = Minecraft.getInstance();

    public ServerInfoModule() {
        super("ServerInfo", Category.RENDER);
    }

    public String getServerIP() {
        if (mc.getCurrentServer() == null) return "SP";
        return mc.getCurrentServer().ip;
    }

    public int getPing() {
        if (mc.player == null) return -1;

        PlayerInfo info = mc.player.connection.getPlayerInfo(mc.player.getUUID());

        return info != null ? info.getLatency() : -1;
    }

    public int getPlayerCount() {
        if (mc.player == null) return 0;

        return mc.player.connection.getOnlinePlayers().size();
    }

    public boolean isInMultiplayer() {
        return mc.getCurrentServer() != null && !mc.isLocalServer();
    }



}
