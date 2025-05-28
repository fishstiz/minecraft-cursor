package io.github.fishstiz.minecraftcursor.gui;

public enum MouseEvent {
    CLICK,
    DRAG,
    RELEASE;

    public boolean clicked() {
        return this == CLICK;
    }

    public boolean dragged() {
        return this == DRAG;
    }

    public boolean released() {
        return this == RELEASE;
    }
}
