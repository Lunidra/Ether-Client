package luni.ether.ui.hud.impl;

import luni.ether.ui.component.UIComponent;
import luni.ether.ui.render.UIRenderer;
import luni.ether.ui.theme.ThemeManager;
import net.minecraft.client.Minecraft;

public class HudFpsComponent extends UIComponent {

    public HudFpsComponent() {
        super(
                "fps",
                5,
                38,
                50,
                12
        );
        visibleInClickGui = false;
    }

    @Override
    public void render(UIRenderer r, int mouseX, int mouseY) {

        var mc = Minecraft.getInstance();

        String text =
                "FPS " + mc.getFps();

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