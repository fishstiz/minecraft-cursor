package io.github.fishstiz.minecraftcursor.impl;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import org.jetbrains.annotations.Nullable;

public class CursorControllerImpl implements CursorController {
    private CursorType singleCycleCursor;

    public boolean hasTransientCursor() {
        return singleCycleCursor != null;
    }

    public @Nullable CursorType consumeTransientCursor() {
        CursorType cursorType = singleCycleCursor;
        singleCycleCursor = null;
        return cursorType;
    }

    @Override
    public void setSingleCycleCursor(CursorType cursorType) {
        this.singleCycleCursor = cursorType;
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
