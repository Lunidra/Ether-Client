package luni.ether.ui.hud.editor;

import luni.ether.core.EtherClient;
import luni.ether.core.config.ConfigManager;
import luni.ether.ui.component.UIComponent;
import luni.ether.ui.render.UIRenderer;
import luni.ether.feature.module.Module;
import luni.ether.ui.theme.ThemeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class HudEditorScreen extends Screen {

    private final List<UIComponent> components =
            new ArrayList<>();

    private float lastMouseX;
    private float lastMouseY;

    private boolean mouseDown = false;

    public HudEditorScreen() {
        super(Component.literal("HUD Editor"));

        components.addAll(
                EtherClient.get()
                        .getContext()
                        .getHudManager()
                        .getComponents()
        );
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event,
                                boolean inside) {

        int button = event.button();

        for (UIComponent c : components) {

            if (!c.isVisible()
                    || !c.isVisibleInHudEditor()) {
                continue;
            }

            if (!c.isMovable()) {
                continue;
            }
                c.mouseClicked(
                        lastMouseX,
                        lastMouseY,
                        button
                );

        }

        mouseDown = true;

        return super.mouseClicked(event, inside);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {

        int button = event.button();

        for (UIComponent c : components) {

            if (!c.isVisible()
                    || !c.isVisibleInHudEditor()) {
                continue;
            }

            if (!c.isMovable()) {
                continue;
            }
            c.mouseReleased(button);
        }

        mouseDown = false;

        return super.mouseReleased(event);
    }

    @Override
    public void render(GuiGraphics gfx,
                       int mouseX,
                       int mouseY,
                       float delta) {

        lastMouseX = mouseX;
        lastMouseY = mouseY;

        UIRenderer r = new UIRenderer(gfx);

        r.rect(
                0,
                0,
                width,
                height,
                ThemeManager.get().getSurface(170)
        );

        String title = "HUD EDITOR";

        int titleWidth =
                Minecraft.getInstance()
                        .font
                        .width(title);

        float titleX =
                (width / 2f) - (titleWidth / 2f);

        r.text(
                title,
                titleX,
                8,
                ThemeManager.get().getAccent(255)
        );

        String helper =
                "Drag components to reposition";

        int helperWidth =
                Minecraft.getInstance()
                        .font
                        .width(helper);

        float helperX =
                (width / 2f) - (helperWidth / 2f);

        r.text(
                helper,
                helperX,
                20,
                ThemeManager.get().getTextSecondary(180)
        );

        // dragging
        if (mouseDown) {
            for (UIComponent c : components) {

                if (!c.isVisible()
                        || !c.isVisibleInHudEditor()) {
                    continue;
                }

                if (!c.isMovable()) {
                    continue;
                }
                c.mouseDragged(
                        lastMouseX,
                        lastMouseY
                );
            }
        }



        // render components
        for (UIComponent c : components) {

            if (!c.isVisible()
                    || !c.isVisibleInHudEditor()) {
                continue;
            }

            boolean hovering =
                    mouseX >= c.getX() &&
                            mouseX <= c.getX() + c.getWidth() &&
                            mouseY >= c.getY() &&
                            mouseY <= c.getY() + c.getHeight();

            int outline =
                    hovering
                            ? ThemeManager.get().getAccent(220)
                            : ThemeManager.get().getBorder(180);

            r.rect(c.getX() - 2, c.getY() - 2, c.getWidth() + 4, c.getHeight() + 4, ThemeManager.get().getSurfaceBright(90));

            c.render(r, mouseX, mouseY);



            if (!c.isMovable()) {
                continue;
            }

            // outline
            r.rect(
                    c.getX(),
                    c.getY(),
                    c.getWidth(),
                    1,
                    outline
            );

            r.rect(
                    c.getX(),
                    c.getY() + c.getHeight(),
                    c.getWidth(),
                    1,
                    outline
            );

            r.rect(
                    c.getX(),
                    c.getY(),
                    1,
                    c.getHeight(),
                    outline
            );

            r.rect(
                    c.getX() + c.getWidth(),
                    c.getY(),
                    1,
                    c.getHeight(),
                    outline
            );
        }

        super.render(gfx, mouseX, mouseY, delta);

        EtherClient.get()
                .getContext()
                .getHudManager()
                .renderNotifications(r);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {

        ConfigManager.saveHud(
                EtherClient.get()
                        .getContext()
                        .getHudManager()
        );

        super.onClose();

        var moduleManager =
                EtherClient.get()
                        .getContext()
                        .getModuleManager();

        Module module =
                moduleManager.getByName("HUDEditor");

        if (module != null && module.isEnabled()) {
            module.setEnabled(false);
        }
    }
}