package luni.ether.ui.notification;

import luni.ether.core.render.UILayer;
import luni.ether.core.render.UIRenderable;
import luni.ether.ui.render.UIRenderer;

public class NotificationOverlay implements UIRenderable {

    @Override
    public UILayer getLayer() {
        return UILayer.OVERLAY;
    }

    @Override
    public void render(UIRenderer renderer, int mouseX, int mouseY) {
        NotificationRenderer.render(renderer);
    }
}