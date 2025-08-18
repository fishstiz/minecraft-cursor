package io.github.fishstiz.testmod.gui.components;

import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.testmod.compat.minecraftcursor.MinecraftCursorUtil;

// Does not need registration, but requires additional care if minecraft-cursor is an optional dependency
public class UnsafeCursorElement extends Buttons.Stub implements CursorProvider {
    private final CursorType cursorType;

    public UnsafeCursorElement(CursorType cursor) {
        super(MinecraftCursorUtil.getTranslation(cursor));

        this.cursorType = cursor;
    }

    @Override
    public CursorType getCursorType(double mouseX, double mouseY) {
        return this.cursorType;
    }
}
