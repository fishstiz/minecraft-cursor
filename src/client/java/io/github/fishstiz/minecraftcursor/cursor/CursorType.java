package io.github.fishstiz.minecraftcursor.cursor;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum CursorType {
    DEFAULT("default"),
    POINTER("pointer"),
    GRABBING("grabbing"),
    TEXT("text"),
    SHIFT("shift"),
    DEFAULT_FORCE("default_force"); // fake cursor type to force default

    private final String key;

    public static final Map<String, CursorType> TYPES = new HashMap<>();

    CursorType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    static {
        for (CursorType cursorType : CursorType.values()) {
            if (cursorType == CursorType.DEFAULT_FORCE) continue;
            TYPES.put(cursorType.key, cursorType);
        }
    }

    @Nullable
    public static CursorType getCursorTypeOrNull(String key) {
        return TYPES.get(key);
    }
}
