package io.github.fishstiz.minecraftcursor.compat;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.stream.Stream;

public class ExternalCursorTracker implements CursorTracker {
    private static final List<Long> INTERNAL_IMAGES = Collections.synchronizedList(new ArrayList<>(2)); // in case of race condition
    private static boolean tracking = false;
    private final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    private final Map<Long, ExternalCursor> externalCursors = new HashMap<>();
    private final Map<Integer, CursorTimestamp> currentCursors = new HashMap<>();
    private boolean hasCustomCursor;

    private ExternalCursorTracker() {
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

    private boolean shouldReplaceCursor(CursorTimestamp current, CursorTimestamp other) {
        if (current == null) { // replace if first cursor
            return true;
        }

        boolean isCurrentCustom = current.cursorType == ExternalCursor.CUSTOM;
        boolean isOtherCustom = other.cursorType == ExternalCursor.CUSTOM;
        if (isOtherCustom != isCurrentCustom) { // replace if custom
            return isOtherCustom;
        }
        if (isCurrentCustom) { // if both custom, replace if timestamp is more recent
            return other.timestamp > current.timestamp;
        }

        if (other.cursorType.isDefault() != current.cursorType.isDefault()) { // replace if non-default
            return !other.cursorType.isDefault();
        }

        return other.timestamp > current.timestamp; // replace if timestamp is more recent
    }

    private CursorType getLatestCursorOrDefault() {
        CursorTimestamp latestCursorTimestamp = null;

        for (CursorTimestamp cursorTimestamp : currentCursors.values()) {
            if (shouldReplaceCursor(latestCursorTimestamp, cursorTimestamp)) {
                latestCursorTimestamp = cursorTimestamp;
            }
        }

        return latestCursorTimestamp != null ? latestCursorTimestamp.cursorType : CursorType.DEFAULT;
    }

    @Override
    public @Nullable ExternalCursor getTrackedCursor(long cursor) {
        return this.externalCursors.get(cursor);
    }

    @Override
    public void untrackCursor(long cursor) {
        this.externalCursors.remove(cursor);
    }

    @Override
    public void updateCursor(int caller, CursorType cursorType) {
        this.updateCursorTimestamp(caller, cursorType);
    }

    @Override
    public boolean isTracking(long cursor) {
        return this.externalCursors.containsKey(cursor);
    }

    @Override
    public @NotNull CursorType getCursorOrDefault() {
        return this.getLatestCursorOrDefault();
    }

    @Override
    public boolean isCustom() {
        return hasCustomCursor && getCursorOrDefault() == ExternalCursor.CUSTOM;
    }

    private static class Holder {
        private static final ExternalCursorTracker INSTANCE = new ExternalCursorTracker();

        static {
            tracking = true;
            MinecraftCursor.LOGGER.info("[minecraft-cursor] Tracking cursors from other mods...");
        }
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

    public static void trackCursor(long cursor, int caller, CursorType cursorType) {
        ExternalCursor externalCursor = Holder.INSTANCE.getTrackedCursor(cursor);

        if (externalCursor == null) {
            externalCursor = new ExternalCursor(caller, cursorType);
            Holder.INSTANCE.externalCursors.put(cursor, externalCursor);
        }
        if (cursorType == ExternalCursor.CUSTOM) {
            Holder.INSTANCE.hasCustomCursor = true;
        }

        externalCursor.update(cursorType);
    }

    public static void trackCursor(long cursor, int caller) {
        trackCursor(cursor, caller, ExternalCursor.CUSTOM);
    }

    public static void trackInternalCursor(long image) {
        INTERNAL_IMAGES.add(image);
    }

    public static boolean consumeInternalCursor(long image) {
        synchronized (INTERNAL_IMAGES) {
            return INTERNAL_IMAGES.remove(image);
        }
    }

    public static StackWalker getWalker() {
        return Holder.INSTANCE.walker;
    }

    public static String getCallerPackage(Stream<StackWalker.StackFrame> frames) {
        return frames.dropWhile(frame -> frame.getDeclaringClass() == GLFW.class)
                .findFirst()
                .map(frame -> frame.getDeclaringClass().getPackageName())
                .orElse("placeholder");
    }

    public static boolean isInternalPackage(String packageName) {
        return packageName.startsWith("io.github.fishstiz.minecraftcursor");
    }
}
