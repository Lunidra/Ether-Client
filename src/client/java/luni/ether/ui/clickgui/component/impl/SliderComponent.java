package luni.ether.ui.clickgui.component.impl;

import luni.ether.feature.setting.impl.NumberSetting;
import luni.ether.ui.component.SettingComponent;
import luni.ether.ui.render.UIRenderer;

public class SliderComponent extends SettingComponent {

    private final NumberSetting setting;
    private boolean dragging = false;

    public SliderComponent(NumberSetting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public float getHeight() {
        return 20f; // SLIDER_HEIGHT
    }

    @Override
    public void render(UIRenderer r, int mouseX, int mouseY, float delta) {

        float sliderX = x + 5;
        float sliderWidth = width - 10;
        float sliderY = y + 12;

        float percent = (setting.get() - setting.getMin()) /
                (setting.getMax() - setting.getMin());

        boolean hovering = isHovering(mouseX, mouseY);

        if (hovering) {
            r.rect(x + 2, y, 1, height, 0x6600E676);
        }

        // 🔥 VALUE TEXT (you lost this earlier — now it's clean)
        String valueText = String.format("%.2f", setting.get());

        r.text(
                setting.getName() + ": " + valueText,
                x + 4,
                y + 3,
                0xFFFFFFFF
        );

        // background
        r.rect(sliderX, sliderY, sliderWidth, 3, 0xFF333333);

        // fill
        r.rect(sliderX, sliderY, sliderWidth * percent, 3, 0xFF00E676);

        // 🔥 dragging logic handled here (not in panel anymore)
        if (dragging) {
            float p = (mouseX - sliderX) / sliderWidth;
            p = Math.max(0f, Math.min(1f, p));

            float value = setting.getMin() +
                    p * (setting.getMax() - setting.getMin());

            setting.set(value);
        }
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        if (button == 0 && isHovering(mouseX, mouseY)) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(int button) {
        dragging = false;
    }
}