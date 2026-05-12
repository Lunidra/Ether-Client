//package ether.client.input;
//
//import ether.client.event.EventHandler;
//import ether.client.event.impl.KeyEvent;
//import ether.client.module.Module;
//import ether.client.module.ModuleManager;
//import ether.client.module.mods.render.Freelook;
//
//import java.util.HashSet;
//import java.util.Set;
//
//
//public class KeybindHandler {
//
//    private static Module listeningModule = null;
//    private static final Set<Integer> pressedKeys = new HashSet<>();
//
//    @EventHandler
//    public void onKey(KeyEvent event) {
//
//        int key = event.getKey();
//
//        // =========================
//        // BINDING MODE
//        // =========================
//        if (listeningModule != null) {
//            listeningModule.setKey(key);
//            listeningModule = null;
//            return;
//        }
//
//        // =========================
//        // KEY PRESS
//        // =========================
//        if (event.getAction() == KeyEvent.Action.PRESS) {
//
//            // prevent spam (already pressed)
//            if (pressedKeys.contains(key)) return;
//
//            pressedKeys.add(key);
//
//            for (Module m : ModuleManager.getModules()) {
//                if (m.getKey() == key) {
//                    m.toggle();
//                }
//            }
//        }
//
//        // =========================
//        // KEY RELEASE
//        // =========================
//        if (event.getAction() == KeyEvent.Action.RELEASE) {
//            pressedKeys.remove(key);
//        }
//    }
//
//    public static void startListening(Module module) {
//        listeningModule = module;
//    }
//
//    public static Module getListeningModule() {
//        return listeningModule;
//    }
//}


package luni.ether.core.input;

import luni.ether.core.EtherClient;
import luni.ether.core.event.EventHandler;
import luni.ether.core.event.impl.KeyEvent;
//import luni.ether.client.gui.clickgui.ClickGuiScreen;
import luni.ether.feature.module.Module;
import luni.ether.feature.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class KeybindHandler {

    private static Module listeningModule = null;
    //private static final Set<Integer> pressedKeys = new HashSet<>();
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
                    System.out.println("[Ether] Bind cancelled for " + listeningModule.getName());
                    listeningModule = null;
                    return;
                }

                // DELETE = unbind
                if (key == GLFW.GLFW_KEY_DELETE) {
                    listeningModule.setKey(-1);
                    System.out.println("[Ether] Unbound " + listeningModule.getName());
                    listeningModule = null;
                    return;
                }

                // normal bind
                listeningModule.setKey(key);
                listeningModule = null;


            }

            return;
        }

//        if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
//
//
//            if (MC.mouseHandler != null) {
//                MC.mouseHandler.releaseMouse();
//            }
//
//            if (MC.screen == null || MC.screen instanceof PauseScreen) {
//                System.out.println("CLICKGUI KEY PRESSED");
//                //MC.setScreen(new ClickGuiScreen());
//            }
//
//
//        }



        // =========================
        // KEY PRESS
        // =========================
        if (event.getAction() == KeyEvent.Action.PRESS) {
            // prevent spam (held key)
            //if (!pressedKeys.add(key)) return;
            // USE MODULE MANAGER (centralized logic)
            EtherClient.get().getContext().getModuleManager().onKeyPressed(key);
            System.out.println("KEY: " + key);
        }

        // =========================
        // KEY RELEASE
        // =========================
        if (event.getAction() == KeyEvent.Action.RELEASE) {
        //    pressedKeys.remove(key);
        }
    }

    // =========================
    // Binding Control
    // =========================
    public static void startListening(Module module) {
        listeningModule = module;
        System.out.println("[Ether] Press a key to bind " + module.getName());
    }
    public static Module getListeningModule() {
        return listeningModule;
    }

    // =========================
    // Safety (IMPORTANT)
    // =========================
    //public static void resetPressedKeys() {
    //    pressedKeys.clear();
    //}
}