package luni.ether.ui.clickgui.component.impl;


import luni.ether.feature.setting.impl.ModeSetting;
import luni.ether.ui.animation.AnimationUtil;
import luni.ether.ui.component.SettingComponent;
import luni.ether.ui.render.UIRenderer;
import luni.ether.ui.theme.ThemeManager;
import net.minecraft.client.Minecraft;

public class ModeComponent extends SettingComponent {

    private final ModeSetting setting;

    private boolean open = false;
    private float anim = 0f;

    private static final float ROW_HEIGHT = 14f;

    public ModeComponent(ModeSetting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public float getHeight() {
        return ROW_HEIGHT + (setting.getModes().length * ROW_HEIGHT);
    }

    @Override
    public void render(UIRenderer r, int mouseX, int mouseY, float delta) {

        // =========================
        // ANIMATION
        // =========================
        float target = open ? 1f : 0f;
        anim = AnimationUtil.animate(
                anim,
                target,
                Math.min(1f, 8f * delta)
        );

        if (Math.abs(anim) < 0.001f) anim = 0f;

        // =========================
        // HEADER
        // =========================
        boolean hoveringHeader = mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + ROW_HEIGHT;

        if (hoveringHeader) {
            r.rect(x + 2, y, 1, ROW_HEIGHT, ThemeManager.get().getAccent(102));
        }

        r.text(setting.getName(), x + 4, y + 3, ThemeManager.get().getText(255));

        String value = setting.get();
        int valueWidth = Minecraft.getInstance().font.width(value);

        r.text(value, x + width - valueWidth - 10, y + 3, ThemeManager.get().getText(170));

        r.text(open ? "v" : ">", x + width - 8, y + 3, ThemeManager.get().getText(255));

        // =========================
        // DROPDOWN (CLIPPED)
        // =========================
        if (anim <= 0.01f) return;

        float dropdownY = y + ROW_HEIGHT;
        float fullHeight = setting.getModes().length * ROW_HEIGHT;
        float visibleHeight = fullHeight * anim;

        //shrink slightly to avoid last-frame overlap
        visibleHeight -= 0.5f;

        r.pushScissor(
                (int)x,
                (int)dropdownY,
                (int)width,
                (int)visibleHeight
        );

        float renderY = dropdownY;

        for (String mode : setting.getModes()) {

            boolean hovering = mouseX >= x && mouseX <= x + width &&
                    mouseY >= renderY && mouseY <= renderY + ROW_HEIGHT;

            boolean selected = mode.equals(setting.get());

            int bg = selected ? ThemeManager.get().getAccent(48) : 0x20000000;

            r.rect(x + 4, renderY, width - 8, ROW_HEIGHT, bg);

            r.text(
                    mode,
                    x + 6,
                    renderY + 3,
                    selected
                            ? ThemeManager.get().getAccent(255)
                            : ThemeManager.get().getText(255)
            );

            renderY += ROW_HEIGHT;
        }

        r.popScissor();
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {

        if (button != 0) return;

        // HEADER CLICK
        if (mouseY >= y && mouseY <= y + ROW_HEIGHT &&
                mouseX >= x && mouseX <= x + width) {

            open = !open;
            return;
        }

        // OPTIONS CLICK
        if (!open) return;

        float optionY = y + ROW_HEIGHT;

        for (String mode : setting.getModes()) {

            if (mouseY >= optionY && mouseY <= optionY + ROW_HEIGHT &&
                    mouseX >= x && mouseX <= x + width) {

                setting.set(mode);
                open = false;
                return;
            }

            optionY += ROW_HEIGHT;
        }
    }
}