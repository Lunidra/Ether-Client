package luni.ether.ui.notification;

public class Notification {

    private final String message;
    private final NotificationType type;

    private final long startTime;
    private final long duration;

    // animation
    private float animation = 0f;
    private float yOffset = 0f;

    public Notification(
            String message,
            NotificationType type,
            long duration
    ) {

        this.message = message;
        this.type = type;
        this.duration = duration;

        this.startTime =
                System.currentTimeMillis();
    }

    // =========================
    // GETTERS
    // =========================

    public String getMessage() {
        return message;
    }

    public NotificationType getType() {
        return type;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    // =========================
    // ANIMATION
    // =========================

    public float getAnimation() {
        return animation;
    }

    public void setAnimation(float animation) {
        this.animation = animation;
    }

    public float getYOffset() {
        return yOffset;
    }

    public void setYOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    // =========================
    // STATE
    // =========================

    public boolean shouldHide() {
        return System.currentTimeMillis()
                - startTime > duration;
    }

    public boolean isExpired() {
        return shouldHide() && animation <= 0.01f;
    }
}