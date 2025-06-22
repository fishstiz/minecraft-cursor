package io.github.fishstiz.minecraftcursor.compat;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import org.jetbrains.annotations.Nullable;

public class ExternalCursor {
    public static final CursorType CUSTOM = CursorType.of("");
    private final int caller;
    private CursorType cursorType;

    public ExternalCursor(int caller, @Nullable CursorType cursorType) {
        this.caller = caller;
        this.update(cursorType);
    }

    public void update(@Nullable CursorType cursorType) {
        this.cursorType = cursorType != null ? cursorType : CUSTOM;
    }

    public int getCaller() {
        return this.caller;
    }

    public CursorType getCursorType() {
        return cursorType;
    }
}
