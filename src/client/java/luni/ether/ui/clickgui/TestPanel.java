package luni.ether.ui.clickgui;

import luni.ether.ui.component.UIComponent;
import luni.ether.ui.render.UIRenderer;
import luni.ether.ui.theme.ThemeManager;

public class TestPanel extends UIComponent {

    public TestPanel() {
        super(50, 50, 120, 80);
    }

    @Override
    public void render(UIRenderer r, int mouseX, int mouseY) {

        var theme = ThemeManager.get();

        int bg = theme.getBackground(120);
        int accent = theme.getAccent(255);

        r.rect(x, y, width, height, bg);
        r.rect(x, y, width, 1, accent);
    }
}