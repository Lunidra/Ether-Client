package luni.ether.core.event.impl;

import luni.ether.core.event.Event;

public class KeyEvent extends Event {

    public enum Action {
        PRESS,
        RELEASE
    }

    private final int key;
    private final Action action;

    public KeyEvent(int key, Action action) {
        this.key = key;
        this.action = action;
    }

    public int getKey() {
        return key;
    }

    public Action getAction() {
        return action;
    }
}