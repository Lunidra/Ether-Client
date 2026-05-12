
package luni.ether.core.input;


import luni.ether.core.EtherClient;

import luni.ether.core.event.impl.KeyEvent;
import luni.ether.ui.clickgui.ClickGuiScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
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

        // USE EVENT SYSTEM INSTEAD
        EtherClient.get()
                .getContext()
                .getEventBus()
                .post(new KeyEvent(key, KeyEvent.Action.PRESS));
    }
}