package luni.ether.ui.clickgui;

import luni.ether.feature.module.Module;
import luni.ether.feature.setting.Setting;
import luni.ether.ui.component.SettingComponent;
import luni.ether.ui.component.SettingComponentFactory;
import luni.ether.ui.component.UIComponent;
import luni.ether.ui.render.UIRenderer;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class SettingsPanel extends UIComponent {

    private final List<SettingComponent> components = new ArrayList<>();

    private float scroll = 0;
    private float scrollTarget = 0;
    private float maxScroll = 0;
    private Module lastModule = null;

    private long lastTime = System.nanoTime();

    public SettingsPanel(float x, float y) {
        super(x, y, 120, 120);
    }

    // =========================
    // BUILD
    // =========================
    private void rebuild(Module module) {
        components.clear();

        for (Setting s : module.getSettings()) {
            SettingComponent c = SettingComponentFactory.create(s);
            if (c != null) {
                components.add(c);
            }
        }
    }

    // =========================
    // RENDER
    // =========================
    @Override
    public void render(UIRenderer r, int mouseX, int mouseY) {

        Module module = ClickGuiScreen.getSelectedModule();
        if (module == null) return;

        float delta = (System.nanoTime() - lastTime) / 1_000_000_000f;
        lastTime = System.nanoTime();

        // 🔥 rebuild ONLY when module changes
        if (module != lastModule) {
            rebuild(module);
            lastModule = module;
        }

        //rebuild(module); // later we optimize this

        // PANEL
        r.panel(x, y, width, height);
        r.text(module.getName(), x + 4, y + 4, 0xFFFFFFFF);

        float headerHeight = 16f;

        r.pushScissor(
                (int)x,
                (int)(y + headerHeight),
                (int)width,
                (int)(height - headerHeight)
        );

        try {
            float yOffset = y + 16 - scroll;

            for (SettingComponent c : components) {

                float h = c.getHeight();

                c.setBounds(x, yOffset, width, h);
                c.render(r, mouseX, mouseY, delta);

                yOffset += h + 1;
            }

            // =========================
            // SCROLL
            // =========================
            float contentHeight = 0f;

            for (SettingComponent c : components) {
                contentHeight += c.getHeight() + 1;
            }
            float visibleHeight = height - 16;

            maxScroll = Math.max(0, contentHeight - visibleHeight);

            if (scrollTarget < 0) scrollTarget = 0;
            if (scrollTarget > maxScroll) scrollTarget = maxScroll;

            scroll += (scrollTarget - scroll) * 0.15f;

        } finally {
            r.popScissor();
        }
    }

    // =========================
    // INPUT
    // =========================
    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        for (SettingComponent c : components) {
            c.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseDragged(float mouseX, float mouseY) {
        for (SettingComponent c : components) {
            c.mouseDragged(mouseX, mouseY);
        }
    }

    @Override
    public void mouseReleased(int button) {
        for (SettingComponent c : components) {
            c.mouseReleased(button);
        }
    }

    public void keyPressed(int key) {
        for (SettingComponent c : components) {
            c.keyPressed(key);
        }
    }

    public void mouseScrolled(double amount) {

        if (maxScroll <= 0) {
            scrollTarget = 0;
            scroll = 0;
            return;
        }

        scrollTarget -= amount * 20f;

        if (scrollTarget < 0) scrollTarget = 0;
        if (scrollTarget > maxScroll) scrollTarget = maxScroll;
    }
}