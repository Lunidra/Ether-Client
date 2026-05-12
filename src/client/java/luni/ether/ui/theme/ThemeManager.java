package luni.ether.ui.theme;

public class ThemeManager {

    private static Theme current = new Theme(
            0x00E676, // accent (green)
            0xFFFFFF, // text
            0x000000  // background base
    );

    public static Theme get() {
        return current;
    }

    public static void set(Theme theme) {
        current = theme;
    }
}