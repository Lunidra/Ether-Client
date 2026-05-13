package luni.ether.ui.notification;

public class Notification {

    private final String message;
    private final long startTime;
    private final long duration;

    public Notification(String message, long duration) {
        this.message = message;
        this.duration = duration;
        this.startTime = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - startTime > duration
                && animation <= 0.01f;
    }

    private float animation = 0f;

    public float getAnimation() {
        return animation;
    }

    public void setAnimation(float animation) {
        this.animation = animation;
    }
}