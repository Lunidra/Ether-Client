package luni.ether.ui.hud;

import luni.ether.ui.clickgui.ClickGuiScreen;
import luni.ether.ui.component.UIComponent;
import luni.ether.ui.render.UIRenderer;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;


public class HUDManager {

    private final List<UIComponent> components = new ArrayList<>();

    boolean inClickGui = Minecraft.getInstance().screen instanceof ClickGuiScreen;

    public void register(UIComponent component) {
        components.add(component);
    }

    public void render(UIRenderer renderer, int mouseX, int mouseY) {

        var mc = Minecraft.getInstance();

        if (mc.screen instanceof ClickGuiScreen) return;

        renderAll(renderer, mouseX, mouseY);
    }

    public void renderAll(UIRenderer renderer, int mouseX, int mouseY) {
        for (UIComponent c : components) {
            c.render(renderer, mouseX, mouseY);
        }
    }
}