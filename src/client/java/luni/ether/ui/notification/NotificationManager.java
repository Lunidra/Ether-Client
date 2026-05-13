package luni.ether.ui.notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {

    private static final List<Notification> notifications =
            new ArrayList<>();

    public static void post(String message) {
        notifications.add(new Notification(message, 3000));
    }

    public static List<Notification> getNotifications() {
        notifications.removeIf(Notification::isExpired);
        return notifications;
    }
}