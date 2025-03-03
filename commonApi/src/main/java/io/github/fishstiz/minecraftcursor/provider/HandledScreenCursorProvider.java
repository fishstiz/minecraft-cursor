package io.github.fishstiz.minecraftcursor.provider;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class HandledScreenCursorProvider {
    private static final HandledScreenCursorProvider INSTANCE = new HandledScreenCursorProvider();
    private CursorType cursorType = CursorType.DEFAULT;

    private HandledScreenCursorProvider() {
    }

    public static CursorType getCursorType() {
        return INSTANCE.cursorType;
    }

    public static void setCursorType(CursorType cursorType) {
        INSTANCE.cursorType = cursorType;
    }
}
