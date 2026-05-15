package luni.ether.ui.notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {

    private static final List<Notification> notifications =
            new ArrayList<>();

    // =========================
    // POST
    // =========================

    public static void post(
            String message,
            NotificationType type
    ) {

        notifications.add(
                new Notification(
                        message,
                        type,
                        2000
                )
        );
    }

    // quick helpers
    public static void info(String msg) {
        post(msg, NotificationType.INFO);
    }

    public static void success(String msg) {
        post(msg, NotificationType.SUCCESS);
    }

    public static void warning(String msg) {
        post(msg, NotificationType.WARNING);
    }

    public static void error(String msg) {
        post(msg, NotificationType.ERROR);
    }

    // =========================
    // ACCESS
    // =========================

    public static List<Notification> getNotifications() {

        notifications.removeIf(Notification::isExpired);

        return notifications;
    }
}