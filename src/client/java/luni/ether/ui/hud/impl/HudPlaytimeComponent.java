package luni.ether.ui.hud.impl;

import luni.ether.ui.component.UIComponent;
import luni.ether.ui.render.UIRenderer;
import luni.ether.ui.theme.ThemeManager;
import net.minecraft.client.Minecraft;

public class HudPlaytimeComponent extends UIComponent {

    private final long startTime =
            System.currentTimeMillis();

    public HudPlaytimeComponent() {
        super(
                "sessiontime",
                5,
                74,
                80,
                12
        );
        visibleInClickGui = false;
    }

    @Override
    public void render(UIRenderer r, int mouseX, int mouseY) {

        long elapsed =
                (System.currentTimeMillis()
                        - startTime) / 1000;

        long hours = elapsed / 3600;
        long minutes = (elapsed % 3600) / 60;
        long seconds = elapsed % 60;

        String text = String.format(
                "Session %02d:%02d:%02d",
                hours,
                minutes,
                seconds
        );

        var mc = Minecraft.getInstance();

        int paddingX = 5;

        width =
                mc.font.width(text)
                        + (paddingX * 2);

        height = 12;

        var theme = ThemeManager.get();

        r.rect(
                x,
                y,
                width,
                height,
                theme.getBackground(100)
        );

        r.rect(
                x + 4,
                y,
                width - 8,
                1,
                theme.getAccent(255)
        );

        r.text(
                text,
                x + paddingX,
                y + 3,
                theme.getText(255)
        );
    }
}