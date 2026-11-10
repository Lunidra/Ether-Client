package luni.ether.ui.hud.impl;

import luni.ether.ui.component.UIComponent;
import luni.ether.ui.render.UIRenderer;
import luni.ether.ui.theme.ThemeManager;
import net.minecraft.client.Minecraft;

public class SocialOverlayComponent
        extends UIComponent {

    private static final float PADDING = 5f;
    private static final float LINE_HEIGHT = 10f;

    public SocialOverlayComponent() {

        super(
                "social_overlay",
                5,
                40,
                120,
                40
        );

        visible = false;
    }

    @Override
    public void render(
            UIRenderer r,
            int mouseX,
            int mouseY
    ) {
        // Chat functionality removed
    }

    private String shortenServer(
            String server
    ) {

        if (server == null) {
            return "Unknown";
        }

        if (server.equalsIgnoreCase(
                "Singleplayer"
        )) {
            return "SP";
        }

        if (server.contains(
                "Hypixel"
        )) {
            return "Hypixel";
        }

        return server;
    }
}