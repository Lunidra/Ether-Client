package luni.ether.feature.module.mods.render;

import luni.ether.feature.module.Category;
import luni.ether.feature.module.Module;
import luni.ether.feature.module.utils.GammaUtil;
import org.lwjgl.glfw.GLFW;

public class Fullbright extends Module {

    private double oldGamma;

    public Fullbright() {
        super("Fullbright", Category.RENDER);
        setKey(GLFW.GLFW_KEY_G); // OK ✅ (no GLFW calls inside)
    }

    @Override
    public void onEnable() {
        oldGamma = GammaUtil.getGamma();
        GammaUtil.setGamma(100.0);
    }

    @Override
    public void onDisable() {
        GammaUtil.setGamma(oldGamma);
    }
}