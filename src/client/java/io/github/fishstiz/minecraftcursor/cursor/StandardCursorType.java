package io.github.fishstiz.minecraftcursor.cursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;

public enum StandardCursorType implements CursorType {
    DEFAULT("default"),
    POINTER("pointer"),
    GRABBING("grabbing"),
    TEXT("text"),
    SHIFT("shift");

    private final String key;

    StandardCursorType(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}
