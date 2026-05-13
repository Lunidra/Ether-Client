package luni.ether.ui.hud.impl;

import luni.ether.ui.component.UIComponent;
import luni.ether.ui.render.UIRenderer;
import luni.ether.ui.theme.ThemeManager;
import net.minecraft.client.Minecraft;

public class Watermark extends UIComponent {

    public Watermark() {

        super("watermark", 2, 5, 0, 14);
        this.animatedY = 0f; // or set to normalY on first render


    }



    private float animatedY;
    private float targetY;

    @Override
    public void render(UIRenderer r, int mouseX, int mouseY) {

        var mc = Minecraft.getInstance();

        // detect chat
        boolean inChat = mc.screen instanceof net.minecraft.client.gui.screens.ChatScreen;

        // normal position (bottom left)
        float normalY = mc.getWindow().getGuiScaledHeight() - 15;

        // lifted position (above chat box)
        float liftedY = normalY - 14; // tweak if needed

        targetY = inChat ? liftedY : normalY;

        int screenHeight = mc.getWindow().getGuiScaledHeight();

        float speed = 0.15f;
        animatedY += (targetY - animatedY) * speed;

        // snap to avoid jitter
        if (Math.abs(targetY - animatedY) < 0.5f) {
            animatedY = targetY;
        }

        if (animatedY == 0f) {
            animatedY = normalY;
        }

        String text = "Ether";

        int textWidth = mc.font.width(text);

        int paddingX = 5;
        int paddingTop = 2;
        int accentSpacing = 2;

        float bottomPadding = 5f;

        float renderY = screenHeight - height - bottomPadding;

        width = textWidth + paddingX * 2;
        height = 12; // force consistent height



        var theme = ThemeManager.get();

        int bg = theme.getBackground(100); // tweak this value

        r.rect(x, animatedY, width, height, bg);


        // accent closer to top
        r.line(x + 4, animatedY, width - 8, 1, theme.getAccent(255));

        // text slightly lower
        r.text(text, x + paddingX, animatedY + 3, theme.getText(255));

    }
    @Override
    public boolean isMovable() {
        return false;
    }
}