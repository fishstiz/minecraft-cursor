package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import io.github.fishstiz.minecraftcursor.cursor.AnimationState;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;

import java.util.HashMap;
import java.util.Map;

public class CursorAnimationHelper {
    private static final int CURSOR_SIZE = 32;
    private final Map<AnimatedCursor, AnimationState> cursorStates = new HashMap<>();

    public void reset(AnimatedCursor cursor) {
        cursorStates.computeIfAbsent(cursor, c -> new AnimationState()).reset();
    }

    public void drawSprite(DrawContext context, Cursor cursor, int x, int y, int size) {
        int frameIndex = 0;

        if (cursor instanceof AnimatedCursor animatedCursor) {
            frameIndex = getCurrentSpriteIndex(animatedCursor);
        }

        int vOffset = CURSOR_SIZE * frameIndex;

        context.drawTexture(
                RenderLayer::getGuiTextured,
                cursor.getSprite(),
                x, y,
                0, vOffset, // starting point
                size, size, // width/height to stretch/shrink
                CURSOR_SIZE, CURSOR_SIZE, // cropped width/height from actual image
                cursor.getTrueWidth(), cursor.getTrueHeight() // actual width/height
        );
    }

    private int getCurrentSpriteIndex(AnimatedCursor cursor) {
        AnimationState state = cursorStates.computeIfAbsent(cursor, c -> new AnimationState());

        if (!cursor.isAnimated()) {
            state.reset();
            return 0;
        }

        state.nextFrame(cursor);
        return cursor.getFrame(state.getCurrentFrame()).spriteIndex();
    }
}