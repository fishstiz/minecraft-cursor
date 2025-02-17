package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.CursorTypeRegistrar;

import java.util.LinkedHashMap;
import java.util.List;

public class CursorTypeRegistry implements CursorTypeRegistrar {
    private static final LinkedHashMap<String, CursorType> TYPES = new LinkedHashMap<>();

    CursorTypeRegistry() {
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

    public static List<CursorType> types() {
        return List.copyOf(TYPES.values());
    }
}