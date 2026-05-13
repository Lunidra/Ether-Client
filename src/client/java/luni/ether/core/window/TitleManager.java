package luni.ether.core.window;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class TitleManager {

    public static final String NAME = "Ether";
    public static final String VERSION = "1.2";
    public static final String BUILD = "05x26-b5_ALPHA";
    public static final String MC_VER = "26.1";

    private String lastTitle = "";

    public void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            String title = buildTitle();

            if (!title.equals(lastTitle)) {
                client.getWindow().setTitle(title);
                lastTitle = title;
            }

            if (client.player == null && client.level == null) return;


        });
    }

    public static String buildTitle() {
        Minecraft mc = Minecraft.getInstance();
        return "Minecraft " + MC_VER + " (" + NAME + ") Version " + VERSION + " Build " + BUILD + " | " + mc.getFps() + " FPS";
    }
}