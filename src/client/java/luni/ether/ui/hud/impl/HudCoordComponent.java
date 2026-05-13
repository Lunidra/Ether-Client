package luni.ether.ui.hud.impl;

import luni.ether.ui.component.UIComponent;
import luni.ether.ui.render.UIRenderer;
import luni.ether.ui.theme.ThemeManager;
import net.minecraft.client.Minecraft;

public class HudCoordComponent extends UIComponent {

    public HudCoordComponent() {
        super(
                "coordinates",
                5,
                20,
                60,
                12
        );
    }

    @Override
    public void render(UIRenderer r, int mouseX, int mouseY) {

        var mc = Minecraft.getInstance();

        if (mc.player == null) {
            return;
        }

        int xPos = (int) mc.player.getX();
        int yPos = (int) mc.player.getY();
        int zPos = (int) mc.player.getZ();

        String text = String.format(
                "XYZ %d %d %d",
                xPos,
                yPos,
                zPos
        );

        int paddingX = 5;

        width = mc.font.width(text) + (paddingX * 2);
        height = 12;

        var theme = ThemeManager.get();

        // background
        r.rect(
                x,
                y,
                width,
                height,
                theme.getBackground(100)
        );

        // accent line
        r.rect(
                x + 4,
                y,
                width - 8,
                1,
                theme.getAccent(255)
        );

        // text
        r.text(
                text,
                x + paddingX,
                y + 3,
                theme.getText(255)
        );
    }
}