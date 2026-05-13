package luni.ether.feature.module;

import luni.ether.core.EtherClient;
import luni.ether.feature.setting.Setting;
import luni.ether.ui.notification.NotificationManager;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {

    private final String name;
    private final Category category;
    private int key = -1;

    private boolean enabled;

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    // === Lifecycle ===
    public void onEnable() {

        //NotificationManager.post(getName() + " Enabled");
    }
    public void onDisable() {
        //NotificationManager.post(getName() + " Disabled");
    }
    public void onToggle(boolean enabled) {
        //NotificationManager.post(getName() + " Toggled");
    }

    // === Events ===
    public void onTick() {}
    public void onRender() {}

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;

        this.enabled = enabled;

        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }

        onToggle(enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    // === Info ===
    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    // === Access ===
    protected EtherClient getClient() {
        return EtherClient.get();
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getKey() {
        //return KeybindSetting.get();
        return key;
    }

    private final List<Setting> settings = new ArrayList<>();

    public List<Setting> getSettings() {
        return settings;
    }

    protected <T extends Setting> T addSetting(T setting) {
        settings.add(setting);
        return setting;
    }
}