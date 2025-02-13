package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.CursorTypeRegistrar;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

public class CursorTypeRegistry implements CursorTypeRegistrar {
    private static final LinkedHashMap<String, CursorType> TYPES = new LinkedHashMap<>();

    CursorTypeRegistry() {
    }

    static {
        addEntries(
                CursorType.DEFAULT,
                CursorType.POINTER,
                CursorType.GRABBING,
                CursorType.TEXT,
                CursorType.SHIFT
        );
    }

    @Override
    public void register(CursorType... cursorTypes) {
        addEntries(cursorTypes);
    }

    @Override
    public CursorType register(String key) {
        return addEntry(key);
    }

    private static CursorType addEntry(String key) {
        return TYPES.computeIfAbsent(key, CursorType::of);
    }

    private static void addEntries(CursorType... cursorTypes) {
        for (CursorType cursorType : cursorTypes) {
            TYPES.put(cursorType.getKey(), cursorType);
        }
    }

    public static Collection<CursorType> types() {
        return Collections.unmodifiableCollection(TYPES.values());
    }

    public static @Nullable CursorType getCursorTypeOrNull(String key) {
        return TYPES.get(key);
    }
}