package io.github.fishstiz.minecraftcursor.compat;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CursorTracker {
    @Nullable ExternalCursor getTrackedCursor(long cursor);
    void untrackCursor(long cursor);
    void updateCursor(int caller, CursorType cursorType);
    boolean isTracking(long cursor);
    void storeAddress(long address);
    boolean consumeAddress(long address);
    @NotNull CursorType getCursorOrDefault();
    boolean isCustom();
}
