package luni.ether.ui.hud;

import luni.ether.ui.clickgui.ClickGuiScreen;
import luni.ether.ui.component.UIComponent;
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
        for (UIComponent c : components) {
            c.render(renderer, mouseX, mouseY);

        }
    }

    public void renderNotifications(UIRenderer renderer) {

        var mc = Minecraft.getInstance();

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int spacing = 6;
        int notifHeight = 18;

        int index = 0;

        for (Notification notification :
                NotificationManager.getNotifications()) {

            long elapsed =
                    System.currentTimeMillis()
                            - notification.getStartTime();

            boolean expiring =
                    elapsed > notification.getDuration() - 300;

            float target =
                    expiring ? 0f : 1f;

            float anim =
                    notification.getAnimation();

            anim += (target - anim) * 0.15f;

            notification.setAnimation(anim);

            String text = notification.getMessage();

            int textWidth =
                    mc.font.width(text);

            float width = textWidth + 20;

            float x =
                    screenWidth
                            - width
                            - 10
                            + ((1f - anim) * 40);

            float y =
                    screenHeight
                            - 30
                            - (index * (notifHeight + spacing));

            int alpha =
                    (int)(anim * 255);

            int bg =
                    ((alpha / 2) << 24) | 0x101010;

            int accent =
                    ThemeManager.get().getAccent(alpha);

            int textColor =
                    ThemeManager.get().getText(alpha);

            // background
            renderer.panel(
                    x,
                    y,
                    width,
                    notifHeight
            );

            // accent bar
            renderer.rect(
                    x,
                    y,
                    2,
                    notifHeight,
                    accent
            );

            // text
            renderer.text(
                    text,
                    x + 8,
                    y + 5,
                    textColor
            );

            index++;
        }
    }

    public List<UIComponent> getComponents() {
        return components;
    }
}