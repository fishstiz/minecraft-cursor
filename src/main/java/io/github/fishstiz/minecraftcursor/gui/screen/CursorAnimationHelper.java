package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import io.github.fishstiz.minecraftcursor.cursor.AnimationState;

import java.util.HashMap;
import java.util.Map;

public class CursorAnimationHelper {
    private final Map<AnimatedCursor, AnimationState> cursorStates = new HashMap<>();

    public void reset(AnimatedCursor cursor) {
        cursorStates.computeIfAbsent(cursor, c -> new AnimationState()).reset();
    }

    public int getCurrentSpriteIndex(AnimatedCursor cursor) {
        AnimationState state = cursorStates.computeIfAbsent(cursor, c -> new AnimationState());

        if (!cursor.isAnimated()) {
            state.reset();
            return 0;
        }

        state.nextFrame(cursor);
        return cursor.getFrame(state.getCurrentFrame()).spriteIndex();
    }
}