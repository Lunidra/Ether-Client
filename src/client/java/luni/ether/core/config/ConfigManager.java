package luni.ether.core.config;

import com.google.gson.*;
import luni.ether.core.EtherClient;
import luni.ether.feature.module.Module;
import luni.ether.feature.setting.Setting;
import luni.ether.feature.setting.impl.*;
import luni.ether.ui.clickgui.CategoryPanel;
import luni.ether.ui.clickgui.ClickGuiScreen;
import luni.ether.ui.component.UIComponent;
import luni.ether.ui.hud.HUDManager;
import luni.ether.ui.theme.ThemeManager;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static JsonObject cachedGuiData;

    private static Path getConfigPath() {
        return Path.of("ether", "config.json");
    }

    public static void save() {
        try {
            Path path = getConfigPath();
            Files.createDirectories(path.getParent());

            //TODO: 13/05/2026 -Luni Smarter Save Checks
            JsonObject root;

            if (Files.exists(path)) {
                root = JsonParser.parseReader(
                        new FileReader(path.toFile())
                ).getAsJsonObject();
            } else {
                root = new JsonObject();
            }

            for (Module module : EtherClient.get()
                    .getContext()
                    .getModuleManager()
                    .getModules()) {
                //if (module.getName().equalsIgnoreCase("ClickGUI")) continue;

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

                //if (module.getName().equalsIgnoreCase("ClickGUI")) continue;
                if (!root.has(module.getName())) continue;

                JsonObject modJson = root.getAsJsonObject(module.getName());

                if (root.has("gui")) {
                    cachedGuiData = root.getAsJsonObject("gui");
                }

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

    public static float getPanelX(String category, float defaultX) {

        if (cachedGuiData == null) return defaultX;
        if (!cachedGuiData.has("panels")) return defaultX;

        JsonObject panels = cachedGuiData.getAsJsonObject("panels");

        if (!panels.has(category)) return defaultX;

        return panels
                .getAsJsonObject(category)
                .get("x")
                .getAsFloat();
    }

    public static float getPanelY(String category, float defaultY) {

        if (cachedGuiData == null) return defaultY;
        if (!cachedGuiData.has("panels")) return defaultY;

        JsonObject panels = cachedGuiData.getAsJsonObject("panels");

        if (!panels.has(category)) return defaultY;

        return panels
                .getAsJsonObject(category)
                .get("y")
                .getAsFloat();
    }

    public static void saveGuiState(ClickGuiScreen screen) {

        try {
            Path path = getConfigPath();
            Files.createDirectories(path.getParent());

            JsonObject root;

            if (Files.exists(path)) {
                root = JsonParser.parseReader(
                        new FileReader(path.toFile())
                ).getAsJsonObject();
            } else {
                root = new JsonObject();
            }

            JsonObject guiJson = new JsonObject();
            JsonObject panelsJson = new JsonObject();

            for (CategoryPanel panel : screen.getCategoryPanels()) {

                JsonObject panelJson = new JsonObject();

                panelJson.addProperty("x", panel.getX());
                panelJson.addProperty("y", panel.getY());

                panelsJson.add(panel.getCategory().name(), panelJson);
            }

            guiJson.add("panels", panelsJson);

            root.add("gui", guiJson);
            cachedGuiData = guiJson;

            try (Writer writer = new FileWriter(path.toFile())) {
                GSON.toJson(root, writer);
            }

            System.out.println("[Ether] GUI state saved");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveHud(HUDManager hudManager) {

        try {

            Path path = getConfigPath();

            JsonObject root;

            if (Files.exists(path)) {

                root = JsonParser.parseReader(
                        new FileReader(path.toFile())
                ).getAsJsonObject();

            } else {

                root = new JsonObject();
            }

            JsonObject hudJson = new JsonObject();

            for (UIComponent component :
                    hudManager.getComponents()) {

                if (!component.isMovable()) {
                    continue;
                }

                JsonObject componentJson =
                        new JsonObject();

                componentJson.addProperty(
                        "x",
                        component.getX()
                );

                componentJson.addProperty(
                        "y",
                        component.getY()
                );

                hudJson.add(
                        component.getId(),
                        componentJson
                );
            }

            root.add("hud", hudJson);

            try (Writer writer =
                         new FileWriter(path.toFile())) {

                GSON.toJson(root, writer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void loadHud(HUDManager hudManager) {

        try {

            Path path = getConfigPath();

            if (!Files.exists(path)) {
                return;
            }

            JsonObject root =
                    JsonParser.parseReader(
                            new FileReader(path.toFile())
                    ).getAsJsonObject();

            if (!root.has("hud")) {
                return;
            }

            JsonObject hudJson =
                    root.getAsJsonObject("hud");

            for (UIComponent component :
                    hudManager.getComponents()) {

                if (!hudJson.has(component.getId())) {
                    continue;
                }

                JsonObject componentJson =
                        hudJson.getAsJsonObject(
                                component.getId()
                        );

                component.setPosition(
                        componentJson.get("x").getAsFloat(),
                        componentJson.get("y").getAsFloat()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveTheme() {

        try {

            Path path = getConfigPath();

            JsonObject root;

            if (Files.exists(path)) {

                root = JsonParser.parseReader(
                        new FileReader(path.toFile())
                ).getAsJsonObject();

            } else {

                root = new JsonObject();
            }

            JsonObject themeJson =
                    new JsonObject();

            themeJson.addProperty(
                    "active",
                    ThemeManager.getCurrentId()
            );

            root.add("theme", themeJson);

            try (Writer writer =
                         new FileWriter(path.toFile())) {

                GSON.toJson(root, writer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadTheme() {

        try {

            Path path = getConfigPath();

            if (!Files.exists(path)) {
                return;
            }

            JsonObject root =
                    JsonParser.parseReader(
                            new FileReader(path.toFile())
                    ).getAsJsonObject();

            if (!root.has("theme")) {
                return;
            }

            JsonObject themeJson =
                    root.getAsJsonObject("theme");

            if (!themeJson.has("active")) {
                return;
            }

            ThemeManager.set(
                    themeJson
                            .get("active")
                            .getAsString()
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasPanelPosition(String category) {

        if (cachedGuiData == null) return false;
        if (!cachedGuiData.has("panels")) return false;

        JsonObject panels = cachedGuiData.getAsJsonObject("panels");

        return panels.has(category);
    }
}