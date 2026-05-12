//package luni.ether.feature.module;
//
//import luni.ether.core.EtherClient;
//import luni.ether.core.event.EventBus;
//import net.minecraft.client.Minecraft;
//import org.lwjgl.glfw.GLFW;
//
//public abstract class Module {
//
//    private final String name;
//    private final Category category;
//
//    private boolean enabled = false;
//    private int key = -1;
//
//    public Module(String name, Category category) {
//        this.name = name;
//        this.category = category;
//    }
//
//    // =========================
//    // Lifecycle
//    // =========================
//
//    public void onEnable() {}
//    public void onDisable() {}
//    public void onToggle() {}
//
//    public void onRender2D() {}
//    public void onRender3D() {}
//
//    // =========================
//    // Core Logic
//    // =========================
//
//    public void toggle() {
//        setEnabled(!enabled);
//    }
//
//    public void setEnabled(boolean enabled) {
//        if (this.enabled == enabled) return;
//
//        this.enabled = enabled;
//
//        try {
//            if (enabled) {
//                // register FIRST so events work inside onEnable
//                EventBus.register(this);
//                onEnable();
//            } else {
//                onDisable();
//                EventBus.unregister(this);
//            }
//
//            onToggle();
//
//        } catch (Exception e) {
//            System.err.println("[Ether] Module crash: " + name);
//            e.printStackTrace();
//
//            // rollback state if something breaks
//            this.enabled = false;
//            EventBus.unregister(this);
//        }
//    }
//
//    // =========================
//    // Getters
//    // =========================
//
//    public boolean isEnabled() {
//        return enabled;
//    }
//
//    public boolean isToggled() { return enabled; }
//
//    public String getName() {
//        return name;
//    }
//
//    public Category getCategory() {
//        return category;
//    }
//
//    public int getKey() {
//        return key;
//    }
//
//    public void setKey(int key) {
//        this.key = key;
//        System.out.println("[Ether] " + name + " bound to " + getKeyName());
//    }
//
//    public String getKeyName() {
//        if (key == -1) return "NONE";
//
//        // 🚨 DO NOT CALL GLFW BEFORE INIT
//        if (EtherClient.get() == null) {
//            return "KEY_" + key;
//        }
//
//        try {
//            String name = GLFW.glfwGetKeyName(key, 0);
//
//            if (name == null) {
//                return "KEY_" + key;
//            }
//
//            return name.toUpperCase();
//
//        } catch (Exception e) {
//            return "KEY_" + key;
//        }
//    }
//}


package luni.ether.feature.module;

import luni.ether.core.EtherClient;
import luni.ether.feature.setting.Setting;
import luni.ether.feature.setting.impl.KeybindSetting;

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
    public void onEnable() {}
    public void onDisable() {}
    public void onToggle(boolean enabled) {}

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