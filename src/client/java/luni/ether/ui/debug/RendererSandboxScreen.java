package luni.ether.ui.debug;

import luni.ether.ui.render.UIRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RendererSandboxScreen extends Screen {

    public RendererSandboxScreen() {
        super(Component.literal("Renderer Sandbox"));
    }

    @Override
    public void render(
            GuiGraphics gfx,
            int mouseX,
            int mouseY,
            float delta
    ) {

        UIRenderer r = new UIRenderer(gfx);

        // =====================================================
        // BACKGROUND
        // =====================================================

        r.rect(
                0,
                0,
                width,
                height,
                0xFF101010
        );

        // =====================================================
        // SURFACE HIERARCHY TEST
        // =====================================================

        r.panel(
                40,
                40,
                220,
                140,
                0xFF1E1E1E
        );

        r.panelHeader(
                40,
                40,
                220,
                24,
                0xFF2A2A2A
        );

        r.text(
                "Ether Surface Test",
                50,
                48,
                0xFFFFFFFF
        );

        r.panel(
                52,
                76,
                196,
                42,
                0xFF252525
        );

        r.text(
                "Secondary Surface",
                64,
                92,
                0xFFAAAAAA
        );

        r.outlinedPanel(
                52,
                126,
                196,
                40,
                0xFF202020,
                0xFF3A86FF
        );

        r.text(
                "Outlined Panel",
                64,
                140,
                0xFFFFFFFF
        );

        // =====================================================
        // TRANSLATION TEST
        // =====================================================

        r.push();

        r.translate(320, 60);

        r.panel(
                0,
                0,
                120,
                40,
                0xFF3A86FF
        );

        r.text(
                "Translated",
                12,
                14,
                0xFFFFFFFF
        );

        r.pop();

        // should NOT move if stack restores correctly
        r.panel(
                320,
                120,
                140,
                40,
                0xFFFF006E
        );

        r.text(
                "Should NOT Move",
                332,
                134,
                0xFFFFFFFF
        );

        // =====================================================
        // SCALE TEST
        // =====================================================

        r.push();

        r.translate(520, 70);

        r.scale(1.5f, 1.5f);

        r.panel(
                0,
                0,
                120,
                40,
                0xFF8338EC
        );

        r.text(
                "Scaled x1.5",
                10,
                14,
                0xFFFFFFFF
        );

        r.pop();

        // =====================================================
        // NESTED TRANSFORM TEST
        // =====================================================

        r.push();

        r.translate(520, 180);

        r.panel(
                0,
                0,
                200,
                120,
                0xFF202020
        );

        r.text(
                "Parent Space",
                12,
                12,
                0xFFFFFFFF
        );

        r.push();

        r.translate(20, 40);

        r.panel(
                0,
                0,
                120,
                40,
                0xFF3A86FF
        );

        r.text(
                "Nested Child",
                10,
                14,
                0xFFFFFFFF
        );

        r.pop();

        r.text(
                "Parent Restored",
                12,
                92,
                0xFFFFFFFF
        );

        r.pop();

        // =====================================================
        // SCISSOR TEST
        // =====================================================

        r.panel(
                40,
                240,
                220,
                100,
                0xFF1E1E1E
        );

        r.panelHeader(
                40,
                240,
                220,
                24,
                0xFF2A2A2A
        );

        r.text(
                "Scissor Test",
                50,
                248,
                0xFFFFFFFF
        );

        r.pushScissor(
                40,
                264,
                220,
                76
        );

        for (int i = 0; i < 20; i++) {

            r.text(
                    "Clipped Line " + i,
                    50,
                    270 + (i * 12),
                    0xFFFFFFFF
            );
        }

        r.popScissor();

        // =====================================================
        // NESTED SCISSOR TEST
        // =====================================================

        r.panel(
                320,
                340,
                260,
                120,
                0xFF1E1E1E
        );

        r.text(
                "Nested Scissor",
                332,
                350,
                0xFFFFFFFF
        );

        r.pushScissor(
                320,
                370,
                260,
                80
        );

        r.panel(
                340,
                380,
                180,
                60,
                0xFF252525
        );

        r.pushScissor(
                360,
                390,
                100,
                40
        );

        for (int i = 0; i < 20; i++) {

            r.text(
                    "Nested Clip " + i,
                    365,
                    395 + (i * 10),
                    0xFFFFFFFF
            );
        }

        r.popScissor();

        r.popScissor();

        // =====================================================
        // ALPHA LAYER TEST
        // =====================================================

        r.panel(
                760,
                60,
                140,
                80,
                0xAA202020
        );

        r.panel(
                790,
                90,
                140,
                80,
                0xAA3A86FF
        );

        r.text(
                "Alpha Blend",
                800,
                120,
                0xFFFFFFFF
        );

        super.render(gfx, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}