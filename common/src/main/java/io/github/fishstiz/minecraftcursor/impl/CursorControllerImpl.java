package io.github.fishstiz.minecraftcursor.impl;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.Util;
import org.jetbrains.annotations.Nullable;

public class CursorControllerImpl implements CursorController {
    private static final long TRANSIENT_EXPIRE_MS = 40; // 2 ticks
    private CursorType singleCycleCursor;
    private long timestamp = 0;

    public boolean hasTransientCursor() {
        return singleCycleCursor != null && Util.getMillis() - timestamp < TRANSIENT_EXPIRE_MS;
    }

    public @Nullable CursorType consumeTransientCursor() {
        if (!hasTransientCursor()) {
            singleCycleCursor = null;
            return null;
        }

        CursorType cursorType = singleCycleCursor;
        singleCycleCursor = null;
        return cursorType;
    }

    @Override
    public void setSingleCycleCursor(CursorType cursorType) {
        this.singleCycleCursor = cursorType;
        this.timestamp = Util.getMillis();
    }

    @Override
    public void overrideCursor(CursorType cursorType, int index) {
        CursorManager.INSTANCE.overrideCurrentCursor(cursorType, index);
    }

    @Override
    public void removeOverride(int index) {
        CursorManager.INSTANCE.removeOverride(index);
    }
}
