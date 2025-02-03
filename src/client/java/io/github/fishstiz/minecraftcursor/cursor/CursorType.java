package io.github.fishstiz.minecraftcursor.cursor;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;

public class CursorType {
    private static final LinkedHashMap<String, CursorType> TYPES = new LinkedHashMap<>();
    public static final CursorType DEFAULT = new CursorType("default");
    public static final CursorType POINTER = new CursorType("pointer");
    public static final CursorType GRABBING = new CursorType("grabbing");
    public static final CursorType TEXT = new CursorType("text");
    public static final CursorType SHIFT = new CursorType("shift");
    public static final CursorType DEFAULT_FORCE = new CursorType("default_force", false); // fake cursor type to force default

    private final String key;

    public CursorType(String key) {
        this(key, true);
    }

    private CursorType(String key, boolean register) {
        this.key = key;
        if (register) {
            TYPES.put(key, this);
        }
    }

    public String getKey() {
        return this.key;
    }

    public static Collection<CursorType> types() {
        return TYPES.values();
    }

    public static @Nullable CursorType getCursorTypeOrNull(String key) {
        return TYPES.get(key);
    }
}