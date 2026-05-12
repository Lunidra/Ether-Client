    ///////////////
   /// EVENTBUS //
  ///////////////
package luni.ether.core.event;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

public class EventBus {

    private static final Map<Class<?>, List<Listener>> listeners = new HashMap<>();

    private static class Listener {

        final Object owner;
        final Priority priority;
        final Consumer<Event> executor;

        Listener(Object owner, Priority priority, Consumer<Event> executor) {
            this.owner = owner;
            this.priority = priority;
            this.executor = executor;
        }
    }

    public static void register(Object obj) {

        // prevent duplicate registration
        unregister(obj);

        for (Method method : obj.getClass().getDeclaredMethods()) {

            if (!method.isAnnotationPresent(EventHandler.class)) continue;

            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1) continue;

            Class<?> eventType = params[0];
            if (!Event.class.isAssignableFrom(eventType)) continue;

            method.setAccessible(true);

            EventHandler handler = method.getAnnotation(EventHandler.class);

            Consumer<Event> executor = event -> {
                try {
                    method.invoke(obj, event);
                } catch (Exception e) {
                    System.err.println("[Ether] Event error in " + obj.getClass().getSimpleName());
                    e.printStackTrace();
                }
            };

            Listener listener = new Listener(obj, handler.priority(), executor);

            listeners
                    .computeIfAbsent(eventType, k -> new ArrayList<>())
                    .add(listener);

            // sort ONCE per insert (fine for now)
            listeners.get(eventType).sort(
                    Comparator.comparing((Listener l) -> l.priority).reversed()
            );
        }
    }

    public static void unregister(Object obj) {
        for (List<Listener> list : listeners.values()) {
            list.removeIf(listener -> listener.owner == obj);
        }
    }

    public static void post(Event event) {

        Class<?> eventClass = event.getClass();

        // snapshot map
        List<Map.Entry<Class<?>, List<Listener>>> entries =
                new ArrayList<>(listeners.entrySet());

        for (Map.Entry<Class<?>, List<Listener>> entry : entries) {

            Class<?> type = entry.getKey();
            if (!type.isAssignableFrom(eventClass)) continue;

            // snapshot listeners
            List<Listener> snapshot = new ArrayList<>(entry.getValue());

            for (Listener listener : snapshot) {

                // cancellation support
                if (event instanceof CancellableEvent c && c.isCancelled()) {
                    return;
                }

                listener.executor.accept(event);
            }
        }
    }
}