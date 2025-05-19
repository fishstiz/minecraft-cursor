package io.github.fishstiz.minecraftcursor.gui;

import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import io.github.fishstiz.minecraftcursor.cursor.AnimationState;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;
import java.util.Map;

public class CursorAnimationHelper {
    private static final int CURSOR_SIZE = 32;
    private final Map<String, AnimationState> cursorStates = new HashMap<>();

    public void reset(AnimatedCursor cursor) {
        cursorStates.computeIfAbsent(cursor.getType().getKey(), t -> new AnimationState()).reset();
    }

    public void drawSprite(GuiGraphics context, Cursor cursor, int x, int y, int size) {
        int frameIndex = 0;

        if (cursor instanceof AnimatedCursor animatedCursor) {
            frameIndex = getCurrentSpriteIndex(animatedCursor);
        }

        int vOffset = CURSOR_SIZE * frameIndex;

        context.blit(
                cursor.getLocation(),
                x, y,
                size, size,
                0, vOffset,
                CURSOR_SIZE, CURSOR_SIZE,
                cursor.getTextureWidth(), cursor.getTextureHeight()
        );
    }

    private int getCurrentSpriteIndex(AnimatedCursor cursor) {
        AnimationState state = cursorStates.computeIfAbsent(cursor.getType().getKey(), t -> new AnimationState());

        if (!cursor.isAnimated() || !cursor.isEnabled()) {
            state.reset();
            return 0;
        }

        state.nextFrame(cursor);
        return cursor.getFrame(state.getCurrentFrame()).spriteIndex();
    }
}