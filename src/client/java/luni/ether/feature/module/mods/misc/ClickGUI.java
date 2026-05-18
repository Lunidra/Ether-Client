package luni.ether.feature.module.mods.misc;

import luni.ether.feature.module.Category;
import luni.ether.feature.module.Module;
import luni.ether.feature.setting.impl.BooleanSetting;
import luni.ether.feature.setting.impl.KeybindSetting;
import luni.ether.feature.setting.impl.ModeSetting;
import luni.ether.feature.setting.impl.NumberSetting;
import luni.ether.ui.clickgui.ClickGuiScreen;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ClickGUI extends Module {

    public ClickGUI() {
        super("ClickGUI", Category.MISC);

        setKey(GLFW.GLFW_KEY_RIGHT_SHIFT); // RShift

        addSetting(new KeybindSetting("Bind", GLFW.GLFW_KEY_RIGHT_SHIFT));
    }

    @Override
    public void onEnable() {

        var mc = Minecraft.getInstance();

        if (mc.player == null || mc.getWindow() == null) {
            setEnabled(false);
            return;
        }

        if (!(mc.screen instanceof ClickGuiScreen)) {
            mc.setScreen(new ClickGuiScreen(this));
        }

        setEnabled(false);
    }



    @Override
    public void onTick() {
        //System.out.println("Tick from TestModule");
    }

    private final BooleanSetting autoBlock =
            addSetting(new BooleanSetting("AutoBlock", true));

    private final NumberSetting range =
            addSetting(new NumberSetting("Range", 3.5f, 1f, 6f, 0.1f));

    private final ModeSetting priority =
            addSetting(new ModeSetting("Priority", "Closest", "Health", "Distance"));
}