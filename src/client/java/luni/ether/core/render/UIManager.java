package luni.ether.core.render;

import luni.ether.ui.render.UIRenderer;

import java.util.ArrayList;
import java.util.List;

public class UIManager {

    private final List<UIRenderable> renderables = new ArrayList<>();

    public void register(UIRenderable renderable) {
        if (!renderables.contains(renderable)) {
            renderables.add(renderable);
            //System.out.println("Registered: " + renderable.getClass().getSimpleName());
        }
    }

    public void unregister(UIRenderable renderable) {
        renderables.remove(renderable);
    }

    public void renderLayer(
            UILayer layer,
            UIRenderer renderer,
            int mouseX,
            int mouseY
    ) {
        //System.out.println("Rendering layer: " + layer);

        renderables.stream()
                .filter(r -> r.getLayer() == layer)
                //.peek(r -> System.out.println("Renderable: " + r.getClass().getSimpleName()))
                .forEach(r -> r.render(renderer, mouseX, mouseY));

    }
}