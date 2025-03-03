package io.github.fishstiz.minecraftcursor.impl;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;

public final class CursorControllerImpl implements CursorController {
    public static final CursorController INSTANCE = new CursorControllerImpl();

    private CursorControllerImpl() {
    }

    @Override
    public void setSingleCycleCursor(CursorType cursorType) {
        MinecraftCursor.getInstance().setSingleCycleCursor(cursorType);
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
