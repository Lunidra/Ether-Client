package luni.ether.ui.theme;

public class Theme {

    private final int accent;
    private final int text;
    private final int background;
    private final String id;

    public Theme(String id, int accent, int text, int background) {
        this.id = id;
        this.accent = accent;
        this.text = text;
        this.background = background;
    }

    public String getId() {
        return id;
    }

    public int getAccent(int alpha) {
        return (alpha << 24) | (accent & 0xFFFFFF);
    }

    public int getText(int alpha) {
        return (alpha << 24) | (text & 0xFFFFFF);
    }

    public int getBackground(int alpha) {
        return (alpha << 24) | (background & 0xFFFFFF);
    }
}