package luni.ether.ui.animation;

public class AnimationUtil {

    // =========================
    // BASIC LERP
    // =========================

    public static float lerp(
            float current,
            float target,
            float speed
    ) {
        return current + (target - current) * speed;
    }

    public static double lerp(
            double current,
            double target,
            double speed
    ) {
        return current + (target - current) * speed;
    }

    // =========================
    // FRAME-INDEPENDENT APPROACH
    // =========================

    public static float animate(
            float current,
            float target,
            float speed
    ) {

        float diff = target - current;

        if (Math.abs(diff) < 0.001f) {
            return target;
        }

        return current + diff * speed;
    }

    public static double animate(
            double current,
            double target,
            double speed
    ) {

        double diff = target - current;

        if (Math.abs(diff) < 0.001) {
            return target;
        }

        return current + diff * speed;
    }

    // =========================
    // CLAMP
    // =========================

    public static float clamp(
            float value,
            float min,
            float max
    ) {
        return Math.max(min, Math.min(max, value));
    }

    public static double clamp(
            double value,
            double min,
            double max
    ) {
        return Math.max(min, Math.min(max, value));
    }

    // =========================
    // SMOOTH STEP
    // =========================

    public static float smoothStep(float t) {

        t = clamp(t, 0f, 1f);

        return t * t * (3f - 2f * t);
    }

    // =========================
    // EASE OUT
    // =========================

    public static float easeOutQuad(float t) {

        t = clamp(t, 0f, 1f);

        return 1f - (1f - t) * (1f - t);
    }
}