package io.github.fishstiz.minecraftcursor.compat;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.util.Util;

import java.util.LinkedHashMap;
import java.util.Objects;

public class ExternalCursorQueue {
    private static final long MIN_DURATION_MS = 50; // 1 tick
    private static ExternalCursorQueue instance;
    private final LinkedHashMap<CursorType, Long> externalCursors = new LinkedHashMap<>();

    private ExternalCursorQueue() {
    }

    private void offerQueue(CursorType cursorType) {
        Objects.requireNonNull(cursorType);

        externalCursors.put(cursorType, 0L);
    }

    private CursorType pollOrDefaultQueue() {
        long currentTime = Util.getMeasuringTimeMs();

        if (externalCursors.size() > 1) {
            externalCursors.entrySet().removeIf(ct -> ct.getValue() != 0L && currentTime > ct.getValue() + MIN_DURATION_MS);
        }

        if (externalCursors.isEmpty()) return CursorType.DEFAULT;

        var latestEntry = externalCursors.lastEntry();
        if (externalCursors.size() > 1 && latestEntry == CursorType.DEFAULT) {
            for (var ctEntry : externalCursors.reversed().sequencedEntrySet()) {
                if (ctEntry.getKey() != CursorType.DEFAULT) {
                    latestEntry = ctEntry;
                    break;
                }
            }
        }

        if (latestEntry.getValue() == 0L) {
            externalCursors.put(latestEntry.getKey(), currentTime);
        } else if (externalCursors.size() > 1 && currentTime > latestEntry.getValue() + MIN_DURATION_MS) {
            externalCursors.remove(latestEntry.getKey());
        }

        return latestEntry.getKey();
    }

    public static void init() {
        if (instance == null) instance = new ExternalCursorQueue();
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public static void offer(CursorType cursorType) {
        if (instance == null) return;
        instance.offerQueue(cursorType);
    }

    public static CursorType pollOrDefault() {
        if (instance == null) return CursorType.DEFAULT;
        return instance.pollOrDefaultQueue();
    }
}
