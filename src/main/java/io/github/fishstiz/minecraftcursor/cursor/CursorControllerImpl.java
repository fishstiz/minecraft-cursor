package io.github.fishstiz.minecraftcursor.cursor;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;

public final class CursorControllerImpl implements CursorController {
    private final CursorManager cursorManager;

    private CursorControllerImpl(CursorManager cursorManager) {
        this.cursorManager = cursorManager;
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

    public static CursorControllerImpl getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final CursorControllerImpl INSTANCE = createInstance();

        private static CursorControllerImpl createInstance() {
            CursorManager manager = MinecraftCursor.getCursorManager();
            if (manager == null) {
                throw new IllegalStateException("CursorController accessed at an invalid time");
            }
            return new CursorControllerImpl(manager);
        }
    }
}
