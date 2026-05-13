package luni.ether.ui.clickgui;

import luni.ether.core.EtherClient;
import luni.ether.core.config.ConfigManager;
import luni.ether.feature.module.Category;
import luni.ether.feature.setting.impl.BooleanSetting;
import luni.ether.ui.component.UIComponent;
import luni.ether.ui.render.UIRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import luni.ether.feature.module.Module;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {

    private final Module module;

    private final List<UIComponent> components = new ArrayList<>();
    private final List<CategoryPanel> categoryPanels = new ArrayList<>();

    private float lastMouseX;
    private float lastMouseY;

    private SettingsPanel settingsPanel;

    private static Module selectedModule;

    private boolean mouseDown = false;

    public ClickGuiScreen(Module module) {
        super(Component.literal("Ether ClickGUI"));
        this.module = module;

        var moduleManager = EtherClient.get().getContext().getModuleManager();

        var mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();

        float startX = 10;
        float startY = 20;

        float x = startX;
        float y = startY;

        float spacing = 6;

        // =========================
        // CATEGORY PANELS
        // =========================
        for (Category category : Category.values()) {

            var modules = moduleManager.getByCategory(category);

            float panelX = x;
            float panelY = y;

            if (ConfigManager.hasPanelPosition(category.name())) {
                panelX = ConfigManager.getPanelX(category.name(), x);
                panelY = ConfigManager.getPanelY(category.name(), y);
            }

            CategoryPanel panel = new CategoryPanel(
                    category,
                    modules,
                    panelX,
                    panelY
            );

            categoryPanels.add(panel);
            components.add(panel);

            x += panel.getWidth() + spacing;

            if (x + panel.getWidth() > screenWidth) {
                x = startX;
                y += panel.getHeight() + 10;
            }
        }

        // =========================
        // SETTINGS PANEL (tracked)
        // =========================
        settingsPanel = new SettingsPanel(0, startY);
        components.add(settingsPanel);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean inside) {

        int button = event.button();

        for (UIComponent c : components) {
            c.mouseClicked(lastMouseX, lastMouseY, button);
        }

        mouseDown = true;

        return super.mouseClicked(event, inside);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {

        int button = event.button();

        for (UIComponent c : components) {
            c.mouseReleased(button);
        }

        mouseDown = false;

        return super.mouseReleased(event);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float delta) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;

        UIRenderer r = new UIRenderer(gfx);

        var mc = Minecraft.getInstance();

        float scaledX = lastMouseX;
        float scaledY = lastMouseY;

        // =========================
        // DRAGGING
        // =========================
        if (mouseDown) {
            for (UIComponent c : components) {
                c.mouseDragged(scaledX, scaledY);
            }
        }

        // =========================
        // 🔥 DYNAMIC SETTINGS PANEL POSITION
        // =========================
        float maxRight = 0;

        for (CategoryPanel panel : categoryPanels) {
            maxRight = Math.max(maxRight, panel.getX() + panel.getWidth());
        }

        float spacing = 10;

        settingsPanel.setX(maxRight + spacing);
        settingsPanel.setY(20);

        // OPTIONAL: clamp to screen
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        if (settingsPanel.getX() + settingsPanel.getWidth() > screenWidth) {
            settingsPanel.setX(screenWidth - settingsPanel.getWidth() - 5);
        }

        // =========================
        // HUD (behind)
        // =========================
        var hudManager = EtherClient.get().getContext().getHudManager();
        hudManager.renderAll(r, mouseX, mouseY);

        // =========================
        // RENDER COMPONENTS
        // =========================
        for (UIComponent c : components) {
            c.render(r, mouseX, mouseY);
        }

        super.render(gfx, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {

        ConfigManager.saveGuiState(this);
        ConfigManager.save();


        super.onClose();
        module.setEnabled(false);
    }

    // =========================
    // MODULE SELECTION
    // =========================
    public static void setSelectedModule(Module module) {
        if (selectedModule == module) {
            selectedModule = null;
        } else {
            selectedModule = module;
        }
    }

    public static Module getSelectedModule() {
        return selectedModule;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {

        settingsPanel.mouseScrolled(deltaY);

        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {

        int keyCode = event.key();

        settingsPanel.keyPressed(keyCode);

        return super.keyPressed(event);
    }

    public List<CategoryPanel> getCategoryPanels() {
        return categoryPanels;
    }


}