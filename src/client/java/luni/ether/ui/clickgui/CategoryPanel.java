package luni.ether.ui.clickgui;

import luni.ether.feature.module.Category;
import luni.ether.feature.module.Module;
import luni.ether.ui.component.UIComponent;
import luni.ether.ui.render.UIRenderer;
import luni.ether.ui.theme.ThemeManager;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class CategoryPanel extends UIComponent {

    private final Category category;
    private final List<Entry> entries = new ArrayList<>();

    private boolean dragging;
    private float dragOffsetX, dragOffsetY;

    public CategoryPanel(Category category, List<Module> modules, float x, float y) {
        super("categorypanel", x, y, 80, 14);
        this.category = category;

        for (Module m : modules) {
            entries.add(new Entry(m));
        }
    }

    @Override
    public void render(UIRenderer r, int mouseX, int mouseY) {

        var mc = Minecraft.getInstance();
        var font = mc.font;

        // =========================
        // WIDTH CALC (FIXED)
        // =========================
        int maxWidth = font.width(category.name());

        for (Entry e : entries) {
            maxWidth = Math.max(maxWidth, font.width(e.module.getName()));

        }

        // padding
        width = maxWidth + 8;



        float yOffset = y;

        // =========================
        // HEADER
        // =========================
        r.panel(x, yOffset, width, 14);
        r.rect(x, yOffset, width, 1, ThemeManager.get().getAccent(255));
        r.text(category.name(), x + 4, yOffset + 3, ThemeManager.get().getText(255));

        yOffset += 14;

        // =========================
        // MODULES
        // =========================
        for (Entry e : entries) {

            Module m = e.module;

            // -------------------------
            // MODULE ROW POSITION
            // -------------------------
            float rowY = yOffset;

            boolean hovering =
                    mouseX >= x && mouseX <= x + width &&
                            mouseY >= rowY && mouseY <= rowY + 12;

            float speed = 0.2f;

            // animations
            e.hover += ((hovering ? 1f : 0f) - e.hover) * speed;


            // -------------------------
            // BACKGROUND
            // -------------------------
            int baseAlpha = 30;
            int hoverAlpha = (int)(50 * e.hover);
            int bgColor = ((baseAlpha + hoverAlpha) << 24);

            r.rect(x, rowY, width, 12, bgColor);

            // -------------------------
            // ACCENT
            // -------------------------
            if (m.isEnabled() || e.hover > 0.1f) {
                int accentAlpha =
                        (int)(255 * Math.max(
                                e.hover,
                                m.isEnabled() ? 1f : 0.3f
                        ));

                int accent =
                        ThemeManager.get()
                                .getAccent(accentAlpha);
                r.rect(x, rowY, 1, 12, accent);
            }

            // -------------------------
            // TEXT
            // -------------------------
            //int baseColor = ThemeManager.get().getText(baseAlpha);;

            int textAlpha =
                    m.isEnabled()
                            ? 255
                            : (int)(180 + (75 * e.hover));

            int color =
                    m.isEnabled()
                            ? ThemeManager.get().getAccent(255)
                            : ThemeManager.get().getText(textAlpha);

            r.text(m.getName(), x + 4, rowY + 2, color);

            // -------------------------
            // ARROW (FIXED POSITION)
            // -------------------------


            // move BELOW module row
            yOffset += 12;

            // -------------------------
            // EXPAND AREA (CORRECT POSITION)
            // -------------------------

        }

        height = yOffset - y;
    }

    // =========================
    // INPUT
    // =========================

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {

        // drag header
        if (isHoveringHeader(mouseX, mouseY) && button == 0) {
            dragging = true;
            dragOffsetX = mouseX - x;
            dragOffsetY = mouseY - y;
        }

        float yOffset = y + 14;

        for (Entry e : entries) {

            Module m = e.module;

            if (mouseX >= x && mouseX <= x + width &&
                    mouseY >= yOffset && mouseY <= yOffset + 12) {

                if (button == 0) {
                    m.toggle();
                }

                if (button == 1) {
                    ClickGuiScreen.setSelectedModule(m);
                }
            }

            yOffset += 12;

        }
    }

    @Override
    public void mouseReleased(int button) {
        dragging = false;
    }

    @Override
    public void mouseDragged(float mouseX, float mouseY) {
        if (dragging) {
            x = mouseX - dragOffsetX;
            y = mouseY - dragOffsetY;
        }
    }

    private boolean isHoveringHeader(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + 14;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    // =========================
    // ENTRY
    // =========================
    private static class Entry {
        Module module;

        float hover = 0f;



        public Entry(Module module) {
            this.module = module;
        }
    }

    public Category getCategory() {
        return category;
    }
}