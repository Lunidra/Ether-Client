package luni.ether.ui.clickgui.component.impl;

import luni.ether.feature.setting.impl.BooleanSetting;
import luni.ether.ui.component.SettingComponent;
import luni.ether.ui.render.UIRenderer;

public class ToggleComponent extends SettingComponent {

    private final BooleanSetting setting;
    private float anim = 0f;

    public ToggleComponent(BooleanSetting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public float getHeight() {
        return 14f;
    }

    @Override
    public void render(UIRenderer r, int mouseX, int mouseY, float delta) {

        float target = setting.get() ? 1f : 0f;
        anim += (target - anim) * Math.min(1f, 8f * delta);

        boolean hovering = isHovering(mouseX, mouseY);

        if (hovering) {
            r.rect(x + 2, y, 1, height, 0x6600E676);
        }

        if (setting.get()) {
            r.rect(x + 2, y, 1, height, 0xFF00E676);
        }

        r.text(setting.getName(), x + 4, y + 3, 0xFFFFFFFF);

        float boxX = x + width - 12;
        float boxY = y + 2;

        r.rect(boxX, boxY, 8, 8, 0xFF555555);

        int fillAlpha = (int)(255 * anim);
        float inset = (1f - anim) * 2f;

        r.rect(
                boxX + inset,
                boxY + inset,
                8 - inset * 2,
                8 - inset * 2,
                (fillAlpha << 24) | 0x00E676
        );
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        if (button == 0 && isHovering(mouseX, mouseY)) {
            setting.toggle();
        }
    }
}