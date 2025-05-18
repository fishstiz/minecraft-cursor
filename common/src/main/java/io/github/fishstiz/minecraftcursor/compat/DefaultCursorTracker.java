package io.github.fishstiz.minecraftcursor.compat;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultCursorTracker implements CursorTracker {
    private static DefaultCursorTracker instance;

    static DefaultCursorTracker getInstance() {
        if (instance == null) {
            instance = new DefaultCursorTracker();
        }
        return instance;
    }

    @Override
    public @Nullable ExternalCursor getTrackedCursor(long cursor) {
        return null;
    }

    @Override
    public void untrackCursor(long cursor) {
        // unsupported
    }

    @Override
    public void updateCursor(int caller, CursorType cursorType) {
        // unsupported
    }

    @Override
    public boolean isTracking(long cursor) {
        return false;
    }

    @Override
    public void claimAddress(long address) {
        // unsupported
    }

    @Override
    public boolean unclaimAddress(long address) {
        return true; // consume address if null
    }

    @Override
    public @NotNull CursorType getCursorOrDefault() {
        return CursorType.DEFAULT;
    }

    @Override
    public boolean isCustom() {
        return false;
    }
}
