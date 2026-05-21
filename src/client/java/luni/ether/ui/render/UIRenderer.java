package luni.ether.ui.render;


import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Stack;

public class UIRenderer {

    private final GuiGraphics gfx;
    private final Stack<int[]> scissorStack = new Stack<>();

    public UIRenderer(GuiGraphics gfx) {
        this.gfx = gfx;
    }

    public void rect(float x, float y, float w, float h, int color) {
        gfx.fill((int)x, (int)y, (int)(x + w), (int)(y + h), color);
    }

    public void text(String text, float x, float y, int color) {
        gfx.drawString(
                net.minecraft.client.Minecraft.getInstance().font,
                text,
                (int)x,
                (int)y,
                color
        );
    }

    public void line(float x, float y, float w, float h, int color) {
        rect(x, y, w, h, color);
    }

    public void panel(float x, float y, float w, float h) {

        // base
        rect(x, y, w, h, 0xCC0F0F0F);

        // subtle inner highlight
        rect(x, y, w, 1, 0x20FFFFFF);

        // subtle bottom shadow
        rect(x, y + h - 1, w, 1, 0x30000000);
    }



    //public void pushScissor(int x, int y, int width, int height) {
    //    gfx.enableScissor(x, y, x + width, y + height);
    //}

    public void pushScissor(int x, int y, int width, int height) {

        int[] bounds = new int[] {
                x,
                y,
                width,
                height
        };

        scissorStack.push(bounds);

        gfx.enableScissor(
                x,
                y,
                x + width,
                y + height
        );
    }

    //public void popScissor() {
    //    gfx.disableScissor();
    //}

    public void popScissor() {

        if (!scissorStack.isEmpty()) {
            scissorStack.pop();
        }

        if (scissorStack.isEmpty()) {
            gfx.disableScissor();
            return;
        }

        int[] bounds = scissorStack.peek();

        gfx.enableScissor(
                bounds[0],
                bounds[1],
                bounds[0] + bounds[2],
                bounds[1] + bounds[3]
        );
    }

    public void push() {
        gfx.pose().pushMatrix();
    }

    public void pop() {
        gfx.pose().popMatrix();
    }

    public void translate(float x, float y) {
        gfx.pose().translate(x, y);
    }

    public void scale(float x, float y) {
        gfx.pose().scale(x, y);
    }

    public void panel(
            float x,
            float y,
            float width,
            float height,
            int color
    ) {
        rect(x, y, width, height, color);
    }

    public void outlinedPanel(
            float x,
            float y,
            float width,
            float height,
            int background,
            int outline
    ) {

        rect(x, y, width, height, background);

        line(x, y, x + width, y, outline);
        line(x, y + height, x + width, y + height, outline);

        line(x, y, x, y + height, outline);
        line(x + width, y, x + width, y + height, outline);
    }

    public void panelHeader(
            float x,
            float y,
            float width,
            float height,
            int color
    ) {

        rect(x, y, width, height, color);
    }

    //TODO: 21/05/2026: Luni: NEW RENDERING SYSTEMS
}