package luni.ether.feature.setting.impl;

import luni.ether.feature.setting.Setting;

public class ModeSetting extends Setting {

    private final String[] modes;
    private int index;

    public ModeSetting(String name, String... modes) {
        super(name);
        this.modes = modes;
        this.index = 0;
    }

    public String get() {
        return modes[index];
    }

    public void next() {
        index = (index + 1) % modes.length;
    }

    public void previous() {
        index = (index - 1 + modes.length) % modes.length;
    }

    public String[] getModes() {
        return modes;
    }

    public void set(String mode) {
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equalsIgnoreCase(mode)) {
                index = i;
                return;
            }
        }
    }
}