package luni.ether.feature.setting.impl;

import luni.ether.feature.setting.Setting;

public class NumberSetting extends Setting {

    private float value;
    private final float min;
    private final float max;
    private final float increment;

    public NumberSetting(String name, float value, float min, float max, float increment) {
        super(name);
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public float get() {
        return value;
    }

    public void set(float value) {
        value = Math.max(min, Math.min(max, value));

        // snap to increment
        value = Math.round(value / increment) * increment;

        // FIX floating precision
        int precision = (int) Math.ceil(-Math.log10(increment));
        float scale = (float) Math.pow(10, precision);

        value = Math.round(value * scale) / scale;

        this.value = value;
    }

    public float getMin() { return min; }
    public float getMax() { return max; }
}