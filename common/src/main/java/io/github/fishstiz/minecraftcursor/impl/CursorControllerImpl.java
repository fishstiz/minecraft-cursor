package io.github.fishstiz.minecraftcursor.impl;

import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import org.jetbrains.annotations.Nullable;

public final class CursorControllerImpl implements CursorController {
    private final TransientCursor singleCycleCursor = new TransientCursor();
    private final TransientCursor singleCycleFallbackCursor = new TransientCursor();

    public @Nullable CursorType consumeTransientCursor() {
        return this.singleCycleCursor.consume();
    }

    public @Nullable CursorType consumeTransientFallbackCursor() {
        return this.singleCycleFallbackCursor.consume();
    }

    @Override
    public void setSingleCycleCursor(CursorType cursorType) {
        this.singleCycleCursor.set(cursorType);
    }

    @Override
    public void setSingleCycleFallbackCursor(CursorType cursorType) {
        this.singleCycleFallbackCursor.set(cursorType);
    }

    @Override
    public void overrideCursor(CursorType cursorType, int index) {
        CursorManager.INSTANCE.overrideCurrentCursor(cursorType, index);
    }

    @Override
    public void removeOverride(int index) {
        CursorManager.INSTANCE.removeOverride(index);
    }

    private static class TransientCursor {
        private static final long EXPIRE_MS = SharedConstants.MILLIS_PER_TICK * 2L; // 2 ticks
        private @Nullable CursorType cursorType;
        private long timestamp = 0;

        private boolean isValid() {
            return this.cursorType != null && Util.getMillis() - this.timestamp < EXPIRE_MS;
        }

        private @Nullable CursorType consume() {
            CursorType consumed = this.isValid() ? this.cursorType : null;
            this.cursorType = null;
            return consumed;
        }

        private void set(CursorType cursorType) {
            this.cursorType = cursorType;
            this.timestamp = Util.getMillis();
        }
    }
}
