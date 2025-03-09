package io.github.fishstiz.minecraftcursor.gui.screen;

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
                cursor.getSprite(),
                x, y,
                size, size, // width/height to stretch/shrink
                0, vOffset, // starting point
                CURSOR_SIZE, CURSOR_SIZE, // cropped width/height from actual image
                cursor.getTrueWidth(), cursor.getTrueHeight() // actual width/height
        );
    }

    private int getCurrentSpriteIndex(AnimatedCursor cursor) {
        AnimationState state = cursorStates.computeIfAbsent(cursor.getType().getKey(), t -> new AnimationState());

        if (!cursor.isAnimated()) {
            state.reset();
            return 0;
        }

        state.nextFrame(cursor);
        return cursor.getFrame(state.getCurrentFrame()).spriteIndex();
    }
}