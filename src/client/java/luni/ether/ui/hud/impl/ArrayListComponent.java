package luni.ether.ui.hud.impl;

import luni.ether.core.EtherClient;
import luni.ether.ui.animation.AnimationUtil;
import luni.ether.ui.component.UIComponent;
import luni.ether.ui.render.UIRenderer;
import luni.ether.ui.theme.ThemeManager;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class ArrayListComponent extends UIComponent {

    private final List<Entry> entries = new ArrayList<>();

    public ArrayListComponent() {
        super("arraylist",0, 5, 0, 0); // x handled dynamically (right side)
        visibleInHudEditor = false;
    }
    private static final float PADDING = 5f;
    private static final float SPACING = 4f;

    @Override
    public boolean isMovable() {
        return false;
    }

    @Override
    public void render(UIRenderer r, int mouseX, int mouseY) {

        var mc = Minecraft.getInstance();
        var font = mc.font;

        int screenWidth = mc.getWindow().getGuiScaledWidth();

        // TEMP: replace later with ModuleManager
        var moduleManager = EtherClient.get() // remove if wrong
                .getContext()  // adapt to your structure
                .getModuleManager();

        List<String> activeModules = moduleManager.getEnabledModules()
                .stream()
                .map(m -> m.getName())
                .toList();

// mark existing entries
        for (Entry e : entries) {
            e.enabled = activeModules.contains(e.name);
        }

// add new ones
        for (String name : activeModules) {
            if (entries.stream().noneMatch(e -> e.name.equals(name))) {
                entries.add(new Entry(name, screenWidth));
            }
        }

        // sort by width (longest first)
        entries.sort((a, b) ->
                font.width(b.name) - font.width(a.name)
        );

        float yOffset = PADDING;

        // calculate max width ONCE (outside loop)
        int maxWidth = 0;
        for (Entry entry : entries) {
            maxWidth = Math.max(maxWidth, font.width(entry.name));
        }

        // push slightly off edge so accent is visible
        float rightMargin = 2;

        // anchor to right
        float baseX = screenWidth - maxWidth - rightMargin;


        float speed = 0.12f;
        float fadeSpeed = 0.10f;

        for (Entry e : entries) {

            int textWidth = font.width(e.name);

            float alignedX = baseX + (maxWidth - textWidth);

            // decide where it SHOULD go
            if (e.enabled) {
                e.targetX = alignedX;
                e.targetAlpha = 1f;
            } else {
                e.targetX = screenWidth + 20;
                e.targetAlpha = 0f;
            }

            // animate towards target
            e.x = AnimationUtil.animate(
                    e.x,
                    e.targetX,
                    speed
            );


            e.alpha = AnimationUtil.animate(
                    e.alpha,
                    e.targetAlpha,
                    fadeSpeed
            );

            if (Math.abs(e.targetAlpha - e.alpha) < 0.01f) {
                e.alpha = e.targetAlpha;
            }

            // snap to avoid jitter
            if (Math.abs(e.targetX - e.x) < 0.5f) {
                e.x = e.targetX;
            }

            // don't render if fully off-screen
            if (!e.enabled && e.x > screenWidth) continue;

            // render
            int alpha = (int)(e.alpha * 255);

            var theme = ThemeManager.get();

            //TODO: 18/05/2026 Luni: Migrate new theme system

            int bg = theme.getSurfaceBright(alpha / 2);
            int accent = theme.getAccent(alpha);
            int textColor = theme.getTextPrimary(alpha);




            r.rect(e.x - 4, yOffset, textWidth + 8, 1, theme.getBorder(alpha / 2));
            r.rect(e.x - 4, yOffset, textWidth + 6, 10, bg);

            r.rect(e.x - 4, yOffset + 1, 1, 8, accent);

            r.text(
                    e.name,
                    e.x + 0.5f,
                    yOffset + 1.5f,
                    theme.getAccentDark(100)
            );

            r.text(
                    e.name,
                    e.x,
                    yOffset + 1,
                    textColor
            );

            yOffset += 11;
        }
        entries.removeIf(e -> !e.enabled && e.x > screenWidth);
    }

    private static class Entry {
        String name;

        float x;
        float targetX;

        float alpha = 0f;
        float targetAlpha = 1f;

        boolean enabled; // current state

        public Entry(String name, float startX) {
            this.name = name;
            this.x = startX;
            this.targetX = startX;
            this.enabled = true;
        }
    }
}