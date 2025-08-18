package io.github.fishstiz.minecraftcursor.gui;

import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import io.github.fishstiz.minecraftcursor.cursor.AnimationState;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;

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

    public void drawSprite(GuiGraphics guiGraphics, Cursor cursor, int x, int y, int size) {
        if (cursor.isLoaded()) {
            int cursorSize = cursor.getTextureWidth();
            float spriteIndex = cursor instanceof AnimatedCursor animatedCursor ? this.getCurrentSpriteIndex(animatedCursor) : 0;

            guiGraphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    cursor.getLocation(),
                    x, y,
                    0, cursorSize * spriteIndex,
                    size, size,
                    cursorSize, cursorSize,
                    cursor.getTextureWidth(), cursor.getTextureHeight()
            );
        }
    }

    private int getCurrentSpriteIndex(AnimatedCursor cursor) {
        AnimationState state = cursorStates.computeIfAbsent(cursor.getType().getKey(), t -> new AnimationState());

        if (!cursor.isAnimated() || !cursor.isEnabled()) {
            state.reset();
            return cursor.getFallbackFrame().cursor().getTextureIndex();
        }

        return cursor.nextFrame(state).cursor().getTextureIndex();
    }
}