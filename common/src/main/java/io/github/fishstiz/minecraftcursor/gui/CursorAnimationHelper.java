package io.github.fishstiz.minecraftcursor.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import io.github.fishstiz.minecraftcursor.cursor.AnimationState;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import net.minecraft.client.gui.GuiGraphics;

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

            RenderSystem.enableBlend();
            guiGraphics.blit(
                    cursor.getLocation(),
                    x, y,
                    size, size,
                    0, cursorSize * spriteIndex,
                    cursorSize, cursorSize,
                    cursor.getTextureWidth(), cursor.getTextureHeight()
            );
            RenderSystem.disableBlend();
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