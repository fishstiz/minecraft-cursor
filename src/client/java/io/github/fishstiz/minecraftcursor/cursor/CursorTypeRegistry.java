package io.github.fishstiz.minecraftcursor.cursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

public class CursorTypeRegistry {
    private static final LinkedHashMap<String, CursorType> TYPES = new LinkedHashMap<>();

    static {
        TYPES.put(CursorType.DEFAULT.getKey(), CursorType.DEFAULT);
    }

    public static CursorType put(String key) {
        return TYPES.computeIfAbsent(key, Entry::new);
    }

    public static Collection<CursorType> types() {
        return Collections.unmodifiableCollection(TYPES.values());
    }

    public static @Nullable CursorType getCursorTypeOrNull(String key) {
        return TYPES.get(key);
    }

    public static class Entry implements CursorType {
        private final String key;

        private Entry(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }
    }
}