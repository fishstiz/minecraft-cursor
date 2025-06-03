package io.github.fishstiz.minecraftcursor.gui;

import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import io.github.fishstiz.minecraftcursor.cursor.AnimationState;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;

import java.util.HashMap;
import java.util.Map;

public class CursorAnimationHelper {
    private final Map<String, AnimationState> cursorStates = new HashMap<>();

    public void reset(AnimatedCursor cursor) {
        AnimationState cursorState = cursorStates.get(cursor.getTypeKey());
        if (cursorState != null) {
            cursorState.reset();
        }
    }

    public void drawSprite(GuiGraphics context, Cursor cursor, int x, int y, int size) {
        if (!cursor.isLoaded()) return;

        int frameIndex = 0;

        if (cursor instanceof AnimatedCursor animatedCursor) {
            frameIndex = getCurrentSpriteIndex(animatedCursor);
        }

        int cursorSize = cursor.getTextureWidth();
        int vOffset = cursorSize * frameIndex;

        context.blit(
                RenderType::guiTextured,
                cursor.getLocation(),
                x, y,
                0, vOffset,
                size, size,
                cursorSize, cursorSize,
                cursor.getTextureWidth(), cursor.getTextureHeight()
        );
    }

    private int getCurrentSpriteIndex(AnimatedCursor cursor) {
        AnimationState state = cursorStates.computeIfAbsent(cursor.getType().getKey(), t -> new AnimationState());

        if (!cursor.isAnimated() || !cursor.isEnabled()) {
            state.reset();
            return cursor.getFallbackFrame().spriteIndex();
        }

        return cursor.getFrame(state.next(cursor)).spriteIndex();
    }
}