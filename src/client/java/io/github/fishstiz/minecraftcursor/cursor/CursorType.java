package io.github.fishstiz.minecraftcursor.cursor;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum CursorType {
    DEFAULT("default"),
    POINTER("pointer"),
    TEXT("text");

    private final String key;

    static final Map<String, CursorType> CURSOR_TYPES = new HashMap<>();

    CursorType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    static {
        for (CursorType cursorType : CursorType.values())
            CURSOR_TYPES.put(cursorType.key, cursorType);
    }

    @Nullable
    public static CursorType getCursorTypeOrNull(String key) {
        return CURSOR_TYPES.get(key);
    }
}
