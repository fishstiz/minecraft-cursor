package io.github.fishstiz.minecraftcursor.compat;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ExternalCursorTracker implements CursorTracker {
    private static boolean tracking = false;
    private final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    private final Map<Long, ExternalCursor> externalCursors = new HashMap<>();
    private final Map<Integer, CursorTimestamp> currentCursors = new HashMap<>();
    private final LongOpenHashSet addresses = new LongOpenHashSet();

    private ExternalCursorTracker() {
        MinecraftCursor.LOGGER.info("[minecraft-cursor] Tracking cursors from other mods...");
    }

    private static class CursorTimestamp {
        CursorType cursorType;
        long timestamp;

        public CursorTimestamp(CursorType cursorType) {
            this.cursorType = cursorType;
            this.timestamp = Util.getMillis();
        }

        public void update(CursorType cursorType) {
            if (this.cursorType != cursorType) {
                this.timestamp = Util.getMillis();
            }
            this.cursorType = cursorType;
        }
    }

    private void updateCursorTimestamp(int caller, CursorType cursorType) {
        if (cursorType == null) return;

        CursorTimestamp cursorTimestamp = currentCursors.get(caller);
        if (cursorTimestamp == null) {
            currentCursors.put(caller, new CursorTimestamp(cursorType));
        } else {
            cursorTimestamp.update(cursorType);
        }
    }

    private CursorType getLatestCursorOrDefault() {
        CursorTimestamp latestCursorTimestamp = null;

        for (CursorTimestamp cursorTimestamp : currentCursors.values()) {
            // if latest custom cursor
            if ((cursorTimestamp.cursorType == ExternalCursor.PLACEHOLDER_CUSTOM)
                    && (latestCursorTimestamp == null
                    || latestCursorTimestamp.cursorType != ExternalCursor.PLACEHOLDER_CUSTOM
                    || cursorTimestamp.timestamp > latestCursorTimestamp.timestamp)) {
                latestCursorTimestamp = cursorTimestamp;
                continue;
            }
            // if latest non-default cursor
            if ((latestCursorTimestamp == null
                    || latestCursorTimestamp.cursorType != ExternalCursor.PLACEHOLDER_CUSTOM)
                    && (latestCursorTimestamp == null
                    || latestCursorTimestamp.cursorType == CursorType.DEFAULT
                    || (cursorTimestamp.timestamp > latestCursorTimestamp.timestamp
                    && cursorTimestamp.cursorType != CursorType.DEFAULT))) {
                latestCursorTimestamp = cursorTimestamp;
            }
        }

        return latestCursorTimestamp != null ? latestCursorTimestamp.cursorType : CursorType.DEFAULT;
    }

    private static class Holder {
        private static final ExternalCursorTracker INSTANCE = new ExternalCursorTracker();
    }

    public static boolean isTracking() {
        return tracking;
    }

    public static CursorTracker get() {
        if (!isTracking()) {
            return DefaultCursorTracker.getInstance();
        }
        return Holder.INSTANCE;
    }

    public static StackWalker getWalker() {
        tracking = true;
        return Holder.INSTANCE.walker;
    }

    public static void trackCursor(long cursor, int caller, CursorType cursorType) {
        tracking = true;
        Holder.INSTANCE.externalCursors
                .computeIfAbsent(cursor, c -> new ExternalCursor(caller, cursorType))
                .update(cursorType);
    }

    public static void trackCursor(long cursor, int caller) {
        tracking = true;
        Holder.INSTANCE.externalCursors.putIfAbsent(cursor, new ExternalCursor(caller));
    }

    public @Nullable ExternalCursor getTrackedCursor(long cursor) {
        return Holder.INSTANCE.externalCursors.get(cursor);
    }

    public void untrackCursor(long cursor) {
        Holder.INSTANCE.externalCursors.remove(cursor);
    }

    public void updateCursor(int caller, CursorType cursorType) {
        Holder.INSTANCE.updateCursorTimestamp(caller, cursorType);
    }

    public boolean isTracking(long cursor) {
        return Holder.INSTANCE.externalCursors.containsKey(cursor);
    }

    public void storeAddress(long address) {
        Holder.INSTANCE.addresses.add(address);
    }

    public boolean consumeAddress(long address) {
        return Holder.INSTANCE.addresses.removeIf(a -> a == address);
    }

    public @NotNull CursorType getCursorOrDefault() {
        return Holder.INSTANCE.getLatestCursorOrDefault();
    }

    public boolean isCustom() {
        return getCursorOrDefault() == ExternalCursor.PLACEHOLDER_CUSTOM;
    }
}
