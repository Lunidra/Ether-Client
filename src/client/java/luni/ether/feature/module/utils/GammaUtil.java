package luni.ether.feature.module.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;

import java.lang.reflect.Field;

public class GammaUtil {

    private static Field valueField;

    static {
        try {
            valueField = OptionInstance.class.getDeclaredField("value");
            valueField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setGamma(double value) {

        try {

            Minecraft mc = Minecraft.getInstance();
            OptionInstance<Double> gamma = mc.options.gamma();

            valueField.set(gamma, value);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double getGamma() {

        try {

            Minecraft mc = Minecraft.getInstance();
            OptionInstance<Double> gamma = mc.options.gamma();

            return (double) valueField.get(gamma);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1.0;
    }
}