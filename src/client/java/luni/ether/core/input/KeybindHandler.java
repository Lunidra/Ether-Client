package luni.ether.core.input;

import luni.ether.core.EtherClient;
import luni.ether.core.event.EventHandler;
import luni.ether.core.event.impl.KeyEvent;
import luni.ether.feature.module.Module;
import luni.ether.ui.notification.NotificationManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(KeybindHandler.class);

    private static Module listeningModule = null;
    Minecraft MC = Minecraft.getInstance();

    @EventHandler
    public void onKey(KeyEvent event) {

        int key = event.getKey();

        // =========================
        // BINDING MODE
        // =========================
        if (listeningModule != null) {

            if (event.getAction() == KeyEvent.Action.PRESS) {

                // ESC = cancel bind
                if (key == GLFW.GLFW_KEY_ESCAPE) {
                    LOGGER.info("[Ether] Bind cancelled for {}", listeningModule.getName());
                    listeningModule = null;
                    return;
                }

                // DELETE = unbind
                if (key == GLFW.GLFW_KEY_DELETE) {
                    listeningModule.setKey(-1);
                    LOGGER.info("[Ether] Unbound {}", listeningModule.getName());
                    listeningModule = null;
                    return;
                }

                // normal bind
                listeningModule.setKey(key);
                listeningModule = null;


            }

            return;
        }



        // =========================
        // KEY PRESS
        // =========================
        if (event.getAction() == KeyEvent.Action.PRESS) {
            // USE MODULE MANAGER (centralized logic)
            EtherClient.get().getContext().getModuleManager().onKeyPressed(key);
            LOGGER.debug("KEY: {}", key);
        }

        // =========================
        // KEY RELEASE
        // =========================
        if (event.getAction() == KeyEvent.Action.RELEASE) {
        }
    }

    // =========================
    // Binding Control
    // =========================
    public static void startListening(Module module) {
        listeningModule = module;
        LOGGER.info("[Ether] Press a key to bind {}", module.getName());
    }
    public static Module getListeningModule() {
        return listeningModule;
    }

}