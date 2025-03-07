package io.github.fishstiz.minecraftcursor.compat;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.util.Util;

import java.util.HashMap;

public class ExternalCursorTracker {
    private static ExternalCursorTracker instance;
    private final HashMap<Integer, CursorTimestamp> externalCursors = new HashMap<>();

    private ExternalCursorTracker() {
    }

    private static class CursorTimestamp {
        CursorType cursorType;
        long timestamp;

        public CursorTimestamp(CursorType cursorType) {
            this.cursorType = cursorType;
            this.timestamp = Util.getMeasuringTimeMs();
        }

        public void update(CursorType cursorType) {
            this.cursorType = cursorType;
            this.timestamp = Util.getMeasuringTimeMs();
        }
    }

    private void updateCursorTimestamp(int hash, CursorType cursorType) {
        if (cursorType == null) return;

        CursorTimestamp cursorTimestamp = externalCursors.get(hash);
        if (cursorTimestamp == null) {
            externalCursors.put(hash, new CursorTimestamp(cursorType));
        } else {
            cursorTimestamp.update(cursorType);
        }
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
