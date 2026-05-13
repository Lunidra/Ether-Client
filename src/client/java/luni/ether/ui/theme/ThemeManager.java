package luni.ether.ui.theme;

import java.util.LinkedHashMap;
import java.util.Map;

public class ThemeManager {

    private static final Map<String, Theme> themes =
            new LinkedHashMap<>();

    private static Theme current;

    static {

        register(new Theme(
                "green",
                0x00E676,
                0xFFFFFF,
                0x000000
        ));

        register(new Theme(
                "blue",
                0x3D7EFF,
                0xFFFFFF,
                0x000000
        ));

        register(new Theme(
                "purple",
                0xAA66FF,
                0xFFFFFF,
                0x000000
        ));

        current = themes.get("purple");
    }

    public static void register(Theme theme) {
        themes.put(theme.getId(), theme);
    }

    public static Theme get() {
        return current;
    }

    public static void set(String id) {

        Theme theme = themes.get(id);

        if (theme != null) {
            current = theme;
        }
    }

    public static String getCurrentId() {
        return current.getId();
    }

    public static Map<String, Theme> getThemes() {
        return themes;
    }
}