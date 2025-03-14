package io.github.fishstiz.minecraftcursor.compat;

import io.github.fishstiz.minecraftcursor.api.CursorType;

public class ExternalCursor {
    public static final CursorType PLACEHOLDER_CUSTOM = CursorType.of("");
    private final int caller;
    private CursorType cursorType;

    public ExternalCursor(int caller) {
        this.caller = caller;
        this.cursorType = PLACEHOLDER_CUSTOM;
    }

    public ExternalCursor(int caller, CursorType cursorType) {
        this.caller = caller;
        this.cursorType = cursorType;
    }

    public void update(CursorType cursorType) {
        this.cursorType = cursorType;
    }

    public int getCaller() {
        return this.caller;
    }

    public CursorType getCursorType() {
        return cursorType;
    }
}
