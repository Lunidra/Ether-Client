package luni.ether.ui.theme;

public class Theme {

    private final String id;

    // core
    private final int accent;
    private final int accentDark;

    // text
    private final int textPrimary;
    private final int textSecondary;

    // surfaces
    private final int surface;
    private final int surfaceBright;
    private final int border;

    // status
    private final int success;
    private final int warning;
    private final int error;

    public Theme(
            String id,

            int accent,
            int accentDark,

            int textPrimary,
            int textSecondary,

            int surface,
            int surfaceBright,
            int border,

            int success,
            int warning,
            int error
    ) {

        this.id = id;

        this.accent = accent;
        this.accentDark = accentDark;

        this.textPrimary = textPrimary;
        this.textSecondary = textSecondary;

        this.surface = surface;
        this.surfaceBright = surfaceBright;
        this.border = border;

        this.success = success;
        this.warning = warning;
        this.error = error;
    }

    public String getId() {
        return id;
    }

    // accent

    public int getAccent(int alpha) {
        return color(accent, alpha);
    }

    public int getAccentDark(int alpha) {
        return color(accentDark, alpha);
    }

    // text

    public int getTextPrimary(int alpha) {
        return color(textPrimary, alpha);
    }

    public int getTextSecondary(int alpha) {
        return color(textSecondary, alpha);
    }

    // surfaces

    public int getSurface(int alpha) {
        return color(surface, alpha);
    }

    public int getSurfaceBright(int alpha) {
        return color(surfaceBright, alpha);
    }

    public int getBorder(int alpha) {
        return color(border, alpha);
    }

    // status

    public int getSuccess(int alpha) {
        return color(success, alpha);
    }

    public int getWarning(int alpha) {
        return color(warning, alpha);
    }

    public int getError(int alpha) {
        return color(error, alpha);
    }

    // helper

    private int color(int rgb, int alpha) {
        return (alpha << 24) | (rgb & 0xFFFFFF);
    }

    //TODO: BACKWARD COMPATIBILITY BETWEEN OLD THEMES
    public int getBackground(int alpha) {
        return getSurface(alpha);
    }

    public int getText(int alpha) {
        return getTextPrimary(alpha);
    }
}