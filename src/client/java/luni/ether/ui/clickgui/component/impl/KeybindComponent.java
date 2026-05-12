package luni.ether.ui.clickgui.component.impl;

import luni.ether.feature.module.Module;
import luni.ether.feature.setting.impl.KeybindSetting;
import luni.ether.ui.clickgui.ClickGuiScreen;

import luni.ether.ui.component.SettingComponent;
import luni.ether.ui.render.UIRenderer;

public class KeybindComponent extends SettingComponent {

    private final KeybindSetting setting;
    private boolean listening = false;

    public KeybindComponent(KeybindSetting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public float getHeight() {
        return 14f;
    }

    @Override
    public void render(UIRenderer r, int mouseX, int mouseY, float delta) {

        boolean hovering = isHovering(mouseX, mouseY);

        if (hovering) {
            r.rect(x + 2, y, 1, height, 0x6600E676);
        }

        String text = listening
                ? "Press key..."
                : "Bind: " + getKeyName(setting.get());

        r.text(text, x + 4, y + 3, 0xFFFFFFFF);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        if (button == 0 && isHovering(mouseX, mouseY)) {
            listening = true;
        }
    }

    @Override
    public void keyPressed(int key) {
        if (!listening) return;

        setting.set(key);

        // keep module bind in sync
        Module module = ClickGuiScreen.getSelectedModule();
        if (module != null) {
            module.setKey(key);
        }

        listening = false;
    }

    private String getKeyName(int key) {
        String name = org.lwjgl.glfw.GLFW.glfwGetKeyName(key, 0);

        if (name != null) return name.toUpperCase();

        return switch (key) {
            case org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT";
            case org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
            case org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL -> "LCTRL";
            case org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCTRL";
            case org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT -> "LALT";
            case org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_ALT -> "RALT";
            case org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE -> "ESC";
            default -> "KEY_" + key;
        };
    }
}