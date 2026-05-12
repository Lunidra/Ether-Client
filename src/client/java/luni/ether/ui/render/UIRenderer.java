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

//    public void panel(float x, float y, float w, float h) {
//        // subtle transparent background
//        rect(x, y, w, h, 0x66000000);
//    }

    public void panel(float x, float y, float w, float h) {

        // base
        rect(x, y, w, h, 0xCC0F0F0F);

        // subtle inner highlight
        rect(x, y, w, 1, 0x20FFFFFF);

        // subtle bottom shadow
        rect(x, y + h - 1, w, 1, 0x30000000);
    }



    public void pushScissor(int x, int y, int width, int height) {
        gfx.enableScissor(x, y, x + width, y + height);
    }

    public void popScissor() {
        gfx.disableScissor();
    }


}