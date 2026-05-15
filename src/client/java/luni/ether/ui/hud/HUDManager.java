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


    public void renderNotifications(UIRenderer r) {

        var mc = Minecraft.getInstance();

        float xPadding = 6f;
        float yPadding = 6f;

        float notificationWidth = 140f;
        float notificationHeight = 22f;

        float spacing = 4f;

        float screenWidth =
                mc.getWindow()
                        .getGuiScaledWidth();

        float targetY = yPadding;

        for (Notification n :
                NotificationManager.getNotifications()) {

            // =========================
            // TARGET ANIMATION
            // =========================

            float targetAnim =
                    n.shouldHide() ? 0f : 1f;

            n.setAnimation(
                    AnimationUtil.animate(
                            n.getAnimation(),
                            targetAnim,
                            0.15f
                    )
            );

            n.setYOffset(
                    AnimationUtil.animate(
                            n.getYOffset(),
                            targetY,
                            0.15f
                    )
            );

            float anim =
                    AnimationUtil.easeOutQuad(
                            n.getAnimation()
                    );

            // =========================
            // POSITION
            // =========================

            float hiddenX =
                    screenWidth + notificationWidth;

            float visibleX =
                    screenWidth
                            - notificationWidth
                            - xPadding;

            float x = AnimationUtil.lerp(
                    hiddenX,
                    visibleX,
                    anim
            );

            float y =
                    n.getYOffset();

            // =========================
            // COLORS
            // =========================

            int accent;

            switch (n.getType()) {

                case SUCCESS ->
                        accent = 0xFF4CAF50;

                case WARNING ->
                        accent = 0xFFFFC107;

                case ERROR ->
                        accent = 0xFFF44336;

                default ->
                        accent = 0xFF2196F3;
            }

            int bg = 0xCC000000;

            // =========================
            // BACKGROUND
            // =========================

            r.rect(
                    x,
                    y,
                    notificationWidth,
                    notificationHeight,
                    bg
            );

            // accent bar
            r.rect(
                    x,
                    y,
                    2,
                    notificationHeight,
                    accent
            );

            // =========================
            // TEXT
            // =========================

            r.text(
                    n.getMessage(),
                    x + 8,
                    y + 7,
                    0xFFFFFFFF
            );

            // =========================
            // STACKING
            // =========================

            targetY +=
                    notificationHeight
                            + spacing;
        }
    }


    public List<UIComponent> getComponents() {
        return components;
    }
}