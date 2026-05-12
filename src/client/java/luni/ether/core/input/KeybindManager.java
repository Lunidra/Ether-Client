package luni.ether.core.input;

import luni.ether.feature.module.Module;

public class KeybindManager {

    private static Module listeningModule;

    public static void startListening(Module module) {
        listeningModule = module;
    }

    public static boolean isListening() {
        return listeningModule != null;
    }

    public static Module getListeningModule() {
        return listeningModule;
    }

    public static void setKeyPressed(int key) {
        if (listeningModule != null) {
            listeningModule.setKey(key);
            listeningModule = null;
        }
    }

    public static void handleKey(int key) {
        if (listeningModule != null) {
            listeningModule.setKey(key);
            listeningModule = null;
        }
    }
}