package luni.ether.ui.component;

import luni.ether.feature.setting.Setting;
import luni.ether.ui.render.UIRenderer;

public abstract class SettingComponent {

    protected float x, y, width, height;

    public SettingComponent(Setting setting) {}

    public abstract void render(UIRenderer r, int mouseX, int mouseY, float delta);

    public abstract float getHeight();

    public void setBounds(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected boolean isHovering(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    // =========================
    // INPUT (DEFAULT EMPTY)
    // =========================
    public void mouseClicked(float mouseX, float mouseY, int button) {}

    public void mouseReleased(int button) {}

    public void mouseDragged(float mouseX, float mouseY) {}

    // 🔥 THIS FIXES YOUR ERROR
    public void keyPressed(int key) {}
}