package io.github.fishstiz.minecraftcursor.impl;

import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.Util;
import org.jetbrains.annotations.Nullable;

public final class CursorControllerImpl implements CursorController {
    private static final long TRANSIENT_EXPIRE_MS = 100; // 5 ticks
    private CursorType singleCycleCursor;
    private long timestamp = 0;

    public boolean hasTransientCursor() {
        if (singleCycleCursor != null) {
            if (Util.getMillis() - timestamp < TRANSIENT_EXPIRE_MS) {
                return true;
            }
            singleCycleCursor = null;
        }
        return false;
    }

    public @Nullable CursorType consumeTransientCursor() {
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
