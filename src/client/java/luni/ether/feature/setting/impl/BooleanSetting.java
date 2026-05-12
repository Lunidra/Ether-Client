package luni.ether.feature.setting.impl;

import luni.ether.feature.setting.Setting;

public class BooleanSetting extends Setting {

    private boolean value;

    public BooleanSetting(String name, boolean defaultValue) {
        super(name);
        this.value = defaultValue;
    }

    public boolean get() {
        return value;
    }

    public void toggle() {
        value = !value;
    }

    public void set(boolean value) {
        this.value = value;
    }
}