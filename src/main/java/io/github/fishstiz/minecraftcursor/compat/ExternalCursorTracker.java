package io.github.fishstiz.minecraftcursor.compat;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.util.Util;

import java.util.HashMap;

public class ExternalCursorTracker {
    private static ExternalCursorTracker instance;
    private final HashMap<Integer, CursorTimestamp> externalCursors = new HashMap<>();

    private ExternalCursorTracker() {
    }

    private record CursorTimestamp(CursorType cursorType, long timestamp) {
    }

    private void updateCursorTimestamp(int hash, CursorType cursorType) {
        if (cursorType == null) return;
        externalCursors.put(hash, new CursorTimestamp(cursorType, Util.getMeasuringTimeMs()));
    }

    private CursorType getLatestCursorOrDefault() {
        CursorTimestamp latestCursorTimestamp = null;

        for (CursorTimestamp cursorTimestamp : externalCursors.values()) {
            if (latestCursorTimestamp == null
                    || latestCursorTimestamp.cursorType == CursorType.DEFAULT
                    || (cursorTimestamp.timestamp > latestCursorTimestamp.timestamp
                    && cursorTimestamp.cursorType != CursorType.DEFAULT)) {
                latestCursorTimestamp = cursorTimestamp;
            }
        }

        return latestCursorTimestamp != null ? latestCursorTimestamp.cursorType : CursorType.DEFAULT;
    }

    public static void init() {
        if (instance == null) instance = new ExternalCursorTracker();
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public static void updateCursor(int hash, CursorType cursorType) {
        if (instance == null) return;
        instance.updateCursorTimestamp(hash, cursorType);
    }

    public static CursorType getCursorOrDefault() {
        if (instance == null) return CursorType.DEFAULT;
        return instance.getLatestCursorOrDefault();
    }
}
