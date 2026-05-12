package luni.ether.core.config;

import com.google.gson.*;
import luni.ether.core.EtherClient;
import luni.ether.feature.module.Module;
import luni.ether.feature.setting.Setting;
import luni.ether.feature.setting.impl.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path getConfigPath() {
        return Path.of("ether", "config.json");
    }

    public static void save() {
        try {
            Path path = getConfigPath();
            Files.createDirectories(path.getParent());

            JsonObject root = new JsonObject();

            for (Module module : EtherClient.get()
                    .getContext()
                    .getModuleManager()
                    .getModules()) {
                if (module.getName().equalsIgnoreCase("ClickGUI")) continue;

                JsonObject modJson = new JsonObject();

                modJson.addProperty("enabled", module.isEnabled());
                modJson.addProperty("key", module.getKey());

                JsonObject settingsJson = new JsonObject();

                for (Setting setting : module.getSettings()) {

                    if (setting instanceof BooleanSetting b) {
                        settingsJson.addProperty(setting.getName(), b.get());
                    }

                    if (setting instanceof NumberSetting n) {
                        settingsJson.addProperty(setting.getName(), n.get());
                    }

                    if (setting instanceof ModeSetting m) {
                        settingsJson.addProperty(setting.getName(), m.get());
                    }

                    if (setting instanceof KeybindSetting k) {
                        settingsJson.addProperty(setting.getName(), k.get());
                    }
                }

                modJson.add("settings", settingsJson);
                root.add(module.getName(), modJson);
            }

            try (Writer writer = new FileWriter(path.toFile())) {
                GSON.toJson(root, writer);
            }

            System.out.println("[Ether] Config saved");

        } catch (Exception e) {
            System.err.println("[Ether] Failed to save config");
            e.printStackTrace();
        }
    }

    public static void load() {
        try {
            Path path = getConfigPath();
            if (!Files.exists(path)) return;

            JsonObject root = JsonParser.parseReader(new FileReader(path.toFile())).getAsJsonObject();

            for (Module module : EtherClient.get()
                    .getContext()
                    .getModuleManager()
                    .getModules()) {

                if (module.getName().equalsIgnoreCase("ClickGUI")) continue;
                if (!root.has(module.getName())) continue;

                JsonObject modJson = root.getAsJsonObject(module.getName());

                // key
                if (modJson.has("key")) {
                    module.setKey(modJson.get("key").getAsInt());
                }

                // enabled
                if (modJson.has("enabled")) {
                    module.setEnabled(modJson.get("enabled").getAsBoolean());
                }

                // settings
                if (modJson.has("settings")) {
                    JsonObject settingsJson = modJson.getAsJsonObject("settings");

                    for (Setting setting : module.getSettings()) {

                        if (!settingsJson.has(setting.getName())) continue;

                        if (setting instanceof BooleanSetting b) {
                            b.set(settingsJson.get(setting.getName()).getAsBoolean());
                        }

                        if (setting instanceof NumberSetting n) {
                            n.set(settingsJson.get(setting.getName()).getAsFloat());
                        }

                        if (setting instanceof ModeSetting m) {
                            m.set(settingsJson.get(setting.getName()).getAsString());
                        }

                        if (setting instanceof KeybindSetting k) {
                            k.set(settingsJson.get(setting.getName()).getAsInt());
                        }
                    }
                }
            }

            System.out.println("[Ether] Config loaded");

        } catch (Exception e) {
            System.err.println("[Ether] Failed to load config");
            e.printStackTrace();
        }
    }
}