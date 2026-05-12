package luni.ether.feature.setting.impl;

import luni.ether.feature.setting.Setting;

public class KeybindSetting extends Setting {

    private int key;

    public KeybindSetting(String name, int defaultKey) {
        super(name);
        this.key = defaultKey;
    }

    public int get() {
        return key;
    }

    public void set(int key) {
        this.key = key;
    }
}