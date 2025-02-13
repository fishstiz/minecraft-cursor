package io.github.fishstiz.minecraftcursor.impl;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class HandleScreenCursorProvider {
    private static final HandleScreenCursorProvider INSTANCE = new HandleScreenCursorProvider();
    private CursorType cursorType = CursorType.DEFAULT;

    private HandleScreenCursorProvider() {
    }

    public static CursorType getCursorType() {
        return INSTANCE.cursorType;
    }

    public static void setCursorType(CursorType cursorType) {
        INSTANCE.cursorType = cursorType;
    }
}
