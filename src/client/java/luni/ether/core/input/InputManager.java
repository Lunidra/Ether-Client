//package ether.client.input;
//
//import ether.client.event.EventBus;
//import ether.client.event.impl.KeyEvent;
//import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//import net.minecraft.client.Minecraft;
//import org.lwjgl.glfw.GLFW;
//
//import java.util.HashSet;
//import java.util.Set;
//
//public class InputManager {
//
//    private static final Set<Integer> heldKeys = new HashSet<>();
//
//    public static void init() {
//
//        ClientTickEvents.END_CLIENT_TICK.register(client -> {
//
//            long window = Minecraft.getInstance().getWindow().handle();
//
//            for (int key = 32; key < 348; key++) {
//
//                boolean down = GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
//
//                if (down && !heldKeys.contains(key)) {
//
//                    heldKeys.add(key);
//                    EventBus.post(new KeyEvent(key, KeyEvent.Action.PRESS));
//
//                }
//
//                if (!down && heldKeys.contains(key)) {
//
//                    heldKeys.remove(key);
//                    EventBus.post(new KeyEvent(key, KeyEvent.Action.RELEASE));
//
//                }
//
//                if (KeybindManager.isListening()) {
//                    KeybindManager.handleKey(key);
//                    continue;
//                }
//
//            }
//
//        });
//
//    }
//
//}


package luni.ether.core.input;


import luni.ether.core.EtherClient;
import luni.ether.core.event.EventBus;
import luni.ether.core.event.impl.KeyEvent;
import luni.ether.ui.clickgui.ClickGuiScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

/*public class InputManager {

    private static final Set<Integer> heldKeys = new HashSet<>();

    public static void init() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (client.player == null && client.screen != null) return;

            long window = Minecraft.getInstance().getWindow().handle();

            //  focus safety (VERY IMPORTANT)
            boolean focused = GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE;

            if (!focused) {
                heldKeys.clear();
                KeybindHandler.resetPressedKeys();
                return;
            }

            for (int key = 32; key < 348; key++) {

                boolean down = GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;

                // =========================
                // BINDING MODE (FIXED)
                // =========================

                if (KeybindHandler.getListeningModule() != null) {
                    if (down) {
                        EventBus.post(new KeyEvent(key, KeyEvent.Action.PRESS));
                    }
                    continue;
                }

                // =========================
                // NORMAL INPUT
                // =========================

                // PRESS
                if (down && heldKeys.add(key)) {
                    EventBus.post(new KeyEvent(key, KeyEvent.Action.PRESS));
                }

                // RELEASE
                if (!down && heldKeys.remove(key)) {
                    EventBus.post(new KeyEvent(key, KeyEvent.Action.RELEASE));
                }
            }
        });
    }
}*/

import luni.ether.core.EtherClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class InputManager {

    private final Map<Integer, Boolean> keyStates = new HashMap<>();

    public void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            long window = client.getWindow().handle();

            // Loop through ALL module keys instead of hardcoding
            EtherClient.get()
                    .getContext()
                    .getModuleManager()
                    .getModules()
                    .forEach(module -> {

                        int key = module.getKey();
                        if (key == -1) return;

                        boolean isPressed = GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
                        boolean wasPressed = keyStates.getOrDefault(key, false);

                        // Detect initial press (debounce)
                        if (isPressed && !wasPressed) {
                            onKeyPressed(key);
                        }

                        keyStates.put(key, isPressed);
                    });
        });
    }

    private void onKeyPressed(int key) {

        var mc = Minecraft.getInstance();

        if (mc.screen != null && !(mc.screen instanceof ClickGuiScreen)) {
            return;
        }

        // 🔥 USE EVENT SYSTEM INSTEAD
        EtherClient.get()
                .getContext()
                .getEventBus()
                .post(new KeyEvent(key, KeyEvent.Action.PRESS));
    }
}