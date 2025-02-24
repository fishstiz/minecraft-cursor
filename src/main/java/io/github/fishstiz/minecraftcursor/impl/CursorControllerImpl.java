package io.github.fishstiz.minecraftcursor.impl;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;

public final class CursorControllerImpl implements CursorController {
    public static final CursorController INSTANCE = new CursorControllerImpl();
    private final CursorManager cursorManager = CursorManager.getInstance();

    private CursorControllerImpl() {
    }

    @Override
    public void setSingleCycleCursor(CursorType cursorType) {
        MinecraftCursor.setSingleCycleCursor(cursorType);
    }

    @Override
    public void overrideCursor(CursorType cursorType, int index) {
        cursorManager.overrideCurrentCursor(cursorType, index);
    }

    @Override
    public void removeOverride(int index) {
        cursorManager.removeOverride(index);
    }
}
