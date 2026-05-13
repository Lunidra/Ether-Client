package luni.ether.feature.module.mods.misc;

import luni.ether.feature.module.Category;
import luni.ether.feature.module.Module;
import luni.ether.ui.hud.editor.HudEditorScreen;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class HudEditor extends Module {

    public HudEditor() {
        super("HUDEditor", Category.MISC);

        setKey(GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    @Override
    public void setEnabled(boolean enabled) {

        var mc = Minecraft.getInstance();

        if (enabled) {

            mc.setScreen(
                    new HudEditorScreen()
            );

        } else {

            if (mc.screen instanceof HudEditorScreen) {
                mc.screen.onClose();
            }
        }

        super.setEnabled(enabled);
    }
}