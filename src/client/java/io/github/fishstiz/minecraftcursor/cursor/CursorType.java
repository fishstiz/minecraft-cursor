package io.github.fishstiz.minecraftcursor.cursor;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum CursorType {
    DEFAULT("default"),
    POINTER("pointer"),
    TEXT("text");

    private final String name;

    static final Map<String, CursorType> CURSOR_TYPES = new HashMap<>();

    CursorType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    static {
        for (CursorType cursorType : CursorType.values())
            CURSOR_TYPES.put(cursorType.getName(), cursorType);
    }

    @Nullable
    public static CursorType getCursorTypeOrNull(String name) {
        return CURSOR_TYPES.get(name);
    }
}
