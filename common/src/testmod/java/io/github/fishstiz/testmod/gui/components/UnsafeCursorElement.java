package io.github.fishstiz.testmod.gui.components;

import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.testmod.compat.minecraftcursor.MinecraftCursorUtil;
import net.minecraft.client.gui.components.Button;

// Does not need registration, but requires additional care if minecraft-cursor is an optional dependency
public class UnsafeCursorElement extends Button implements CursorProvider {
    private final CursorType cursorType;

    public UnsafeCursorElement(CursorType cursor) {
        super(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, MinecraftCursorUtil.getTranslation(cursor), Buttons::stub, DEFAULT_NARRATION);

        this.cursorType = cursor;
    }

    @Override
    public CursorType getCursorType(double mouseX, double mouseY) {
        return this.cursorType;
    }
}
