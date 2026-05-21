package luni.ether.feature.module.mods.misc;

import luni.ether.feature.module.Category;
import luni.ether.feature.module.Module;
import luni.ether.ui.clickgui.ClickGuiScreen;
import luni.ether.ui.debug.RendererSandboxScreen;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class Sandbox extends Module {
    public Sandbox() {
        super("SandboxHUD", Category.RENDER);
        setKey(GLFW.GLFW_KEY_V);
    }

    @Override
    public void onEnable() {

        var mc = Minecraft.getInstance();

        if (mc.player == null || mc.getWindow() == null) {
            setEnabled(false);
            return;
        }

        if (!(mc.screen instanceof RendererSandboxScreen)) {
            mc.setScreen(new RendererSandboxScreen());
        }

        setEnabled(false);
    }

}
