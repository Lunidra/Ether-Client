package luni.ether.core.render;

import luni.ether.ui.render.UIRenderer;

public interface UIRenderable {

    UILayer getLayer();

    void render(UIRenderer renderer, int mouseX, int mouseY);
}