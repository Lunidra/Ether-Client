package luni.ether.ui.hud;

import luni.ether.core.EtherClient;
import luni.ether.feature.module.mods.render.HUD;
import luni.ether.ui.animation.AnimationUtil;
import luni.ether.ui.clickgui.ClickGuiScreen;
import luni.ether.ui.component.UIComponent;
import luni.ether.ui.hud.editor.HudEditorScreen;
import luni.ether.ui.hud.impl.HudCoordComponent;
import luni.ether.ui.notification.Notification;
import luni.ether.ui.notification.NotificationManager;
import luni.ether.ui.render.UIRenderer;
import luni.ether.ui.theme.ThemeManager;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;


public class HUDManager {

    private final Minecraft mc = Minecraft.getInstance();
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

        boolean inClickGui =
                mc.screen instanceof ClickGuiScreen;

        boolean inHudEditor =
                mc.screen instanceof HudEditorScreen;

        for (UIComponent c : components) {

            if (!c.isVisible()) {
                continue;
            }

            if (inClickGui
                    && !c.isVisibleInClickGui()) {
                continue;
            }

            if (inHudEditor
                    && !c.isVisibleInHudEditor()) {
                continue;
            }

            c.render(renderer, mouseX, mouseY);
        }
    }

    public UIComponent getComponent(String id) {

        for (UIComponent component : components) {

            if (component.getId().equalsIgnoreCase(id)) {
                return component;
            }
        }

        return null;
    }


    public List<UIComponent> getComponents() {
        return components;
    }
}