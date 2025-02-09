package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.CursorTypeRegistrar;
import io.github.fishstiz.minecraftcursor.cursor.StandardCursorType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

public class CursorTypeRegistry implements CursorTypeRegistrar {
    private static final LinkedHashMap<String, CursorType> TYPES = new LinkedHashMap<>();

    CursorTypeRegistry() {
    }

    static {
        for (CursorType cursorType : StandardCursorType.values()) {
            TYPES.put(cursorType.getKey(), cursorType);
        }
    }

    @Override
    public void register(CursorType... cursorTypes) {
        for (CursorType cursorType : cursorTypes) {
            TYPES.put(cursorType.getKey(), cursorType);
        }
    }

    @Override
    public CursorType register(String key) {
        return TYPES.computeIfAbsent(key, CursorType::of);
    }

    public static Collection<CursorType> types() {
        return Collections.unmodifiableCollection(TYPES.values());
    }

    public static @Nullable CursorType getCursorTypeOrNull(String key) {
        return TYPES.get(key);
    }
}