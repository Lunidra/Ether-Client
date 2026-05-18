package luni.ether.ui.theme;

import java.util.LinkedHashMap;
import java.util.Map;

public class ThemeManager {

    private static final Map<String, Theme> themes =
            new LinkedHashMap<>();

    private static Theme current;

    static {

        register(new Theme(
                "purple",

                0xAA66FF, // accent
                0x7A3DCC, // accent dark

                0xFFFFFF, // text primary
                0xBFBFBF, // text secondary

                0x121212, // surface
                0x1E1E1E, // surface bright
                0x2A2A2A, // border

                0x00E676, // success
                0xFFB300, // warning
                0xFF5252  // error
        ));

        register(new Theme(
                "blue",

                0x3D7EFF,
                0x2A5FCC,

                0xFFFFFF,
                0xBFBFBF,

                0x121212,
                0x1E1E1E,
                0x2A2A2A,

                0x00E676,
                0xFFB300,
                0xFF5252
        ));

        register(new Theme(
                "green",

                0x00E676,
                0x00B85C,

                0xFFFFFF,
                0xBFBFBF,

                0x121212,
                0x1E1E1E,
                0x2A2A2A,

                0x00E676,
                0xFFB300,
                0xFF5252
        ));

        register(new Theme(
                "orange",

                0xFF9800,
                0xCC7700,

                0xFFFFFF,
                0xBFBFBF,

                0x121212,
                0x1E1E1E,
                0x2A2A2A,

                0x00E676,
                0xFFB300,
                0xFF5252
        ));

        register(new Theme(
                "pink",

                0xFF4FA3,
                0xCC2F7D,

                0xFFFFFF,
                0xBFBFBF,

                0x121212,
                0x1E1E1E,
                0x2A2A2A,

                0x00E676,
                0xFFB300,
                0xFF5252
        ));

        current = themes.get("pink");
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