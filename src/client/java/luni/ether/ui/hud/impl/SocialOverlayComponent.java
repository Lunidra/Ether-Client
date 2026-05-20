package luni.ether.ui.hud.impl;

import luni.ether.feature.chat.ChatClient;
import luni.ether.feature.chat.EtherUser;
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

        visible = true;
    }

    @Override
    public void render(
            UIRenderer r,
            int mouseX,
            int mouseY
    ) {

        var theme = ThemeManager.get();

        int bg =
                theme.getSurface(160);

        int text =
                theme.getTextPrimary(255);

        int accent =
                theme.getAccent(255);

        var font =
                Minecraft.getInstance().font;

        int online =
                ChatClient.get()
                        .getEtherUsers()
                        .size();

        // dynamic sizing
        float widest = font.width(
                "ETHER • " + online
        );

        for (EtherUser user :
                ChatClient.get()
                        .getEtherUsers()
                        .values()) {

            String line =
                    user.username +
                            " " +
                            shortenServer(user.server);

            widest = Math.max(
                    widest,
                    font.width(line)
            );
        }

        width = widest + PADDING * 2;
        height =
                18 +
                        (online * LINE_HEIGHT) +
                        PADDING;

        // background
        r.rect(
                x,
                y,
                width,
                height,
                bg
        );

        // top accent
        r.rect(
                x,
                y,
                width,
                1,
                accent
        );

        // header
        boolean connected =
                ChatClient.get().isConnected();

        int headerColor =
                connected
                        ? 0xFF55FF55
                        : 0xFFFF5555;

        r.text(
                "ETHER",
                x + PADDING,
                y + 4,
                headerColor
        );

        float etherWidth =
                font.width("ETHER");

        r.text(
                " • " + online,
                x + PADDING + etherWidth,
                y + 4,
                text
        );

        float offset = 16;

        for (EtherUser user :
                ChatClient.get()
                        .getEtherUsers()
                        .values()) {

            String line =
                    user.username +
                            " §7" +
                            shortenServer(user.server);

            r.text(
                    line,
                    x + PADDING,
                    y + offset,
                    text
            );

            offset += LINE_HEIGHT;
        }
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