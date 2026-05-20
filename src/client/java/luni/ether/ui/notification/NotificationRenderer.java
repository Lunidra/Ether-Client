package luni.ether.ui.notification;

import luni.ether.ui.animation.AnimationUtil;
import luni.ether.ui.render.UIRenderer;
import luni.ether.ui.theme.ThemeManager;
import net.minecraft.client.Minecraft;

public class NotificationRenderer {

    private static final float WIDTH = 140;
    private static final float HEIGHT = 26;
    private static final float SPACING = 4;

    public static void render(UIRenderer r) {

        var mc = Minecraft.getInstance();

        float xPadding = 6f;
        float yPadding = 6f;

        float notificationWidth = 140f;
        float notificationHeight = 28f;

        float spacing = 4f;

        float screenWidth =
                mc.getWindow()
                        .getGuiScaledWidth();

        float targetY = yPadding;

        for (Notification n :
                NotificationManager.getNotifications()) {

            // =========================
            // TARGET ANIMATION
            // =========================

            float targetAnim =
                    n.shouldHide() ? 0f : 1f;

            n.setAnimation(
                    AnimationUtil.animate(
                            n.getAnimation(),
                            targetAnim,
                            0.12f
                    )
            );

            n.setYOffset(
                    AnimationUtil.animate(
                            n.getYOffset(),
                            targetY,
                            0.12f
                    )
            );

            float anim =
                    AnimationUtil.easeOutQuad(
                            n.getAnimation()
                    );

            // =========================
            // POSITION
            // =========================

            float hiddenX =
                    screenWidth + notificationWidth;

            float visibleX =
                    screenWidth
                            - notificationWidth
                            - xPadding;

            float x = AnimationUtil.lerp(
                    hiddenX,
                    visibleX,
                    anim
            );

            float y =
                    n.getYOffset();

            // =========================
            // COLORS
            // =========================

            int accent;

            switch (n.getType()) {

                case SUCCESS ->
                        accent = 0xFF4CAF50;

                case WARNING ->
                        accent = 0xFFFFC107;

                case ERROR ->
                        accent = 0xFFF44336;

                default ->
                        accent = 0xFF2196F3;
            }

            int bg = 0xCC000000;

            // =========================
            // BACKGROUND
            // =========================

            r.rect(
                    x,
                    y,
                    notificationWidth,
                    notificationHeight,
                    bg
            );

            // accent bar
            r.rect(
                    x,
                    y,
                    2,
                    notificationHeight,
                    accent
            );

            // =========================
            // TEXT
            // =========================

// title
            r.text(
                    n.getTitle(),
                    x + 8,
                    y + 4,
                    accent
            );

// message
            r.text(
                    n.getMessage(),
                    x + 8,
                    y + 13,
                    0xFFFFFFFF
            );

            // =========================
            // STACKING
            // =========================

            targetY +=
                    notificationHeight
                            + spacing;
        }
    }

    private static int getAccentColor(
            NotificationType type
    ) {

        return switch (type) {

            case INFO ->
                    0xFF3D7EFF;

            case SUCCESS ->
                    0xFF55FF55;

            case WARNING ->
                    0xFFFFAA00;

            case ERROR ->
                    0xFFFF5555;
        };
    }
}