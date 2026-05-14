package luni.ether.feature.module.utils;

import luni.ether.mixin.OptionInstanceAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;

@SuppressWarnings("unchecked")
public class GammaUtil {

    public static void setGamma(double value) {

        Minecraft mc =
                Minecraft.getInstance();

        OptionInstance<Double> gamma =
                mc.options.gamma();

        ((OptionInstanceAccessor<Double>) (Object) gamma)
                .setValue(value);
    }

    public static double getGamma() {

        Minecraft mc =
                Minecraft.getInstance();

        OptionInstance<Double> gamma =
                mc.options.gamma();

        return ((OptionInstanceAccessor<Double>) (Object) gamma)
                .getValue();
    }
}