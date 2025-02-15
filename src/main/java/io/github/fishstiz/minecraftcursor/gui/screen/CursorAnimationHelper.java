package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import net.minecraft.util.Util;

import java.util.HashMap;
import java.util.Map;

public class CursorAnimationHelper {
    private static class CursorAnimationState {
        long lastFrameTime;
        int currentFrame;
        boolean oscillateReverse;

        CursorAnimationState(long startTime) {
            this.lastFrameTime = startTime;
            this.currentFrame = 0;
            this.oscillateReverse = false;
        }
    }

    private final Map<AnimatedCursor, CursorAnimationState> cursorStates = new HashMap<>();

    public void setCurrentFrame(AnimatedCursor cursor, int frameIndex) {
        long currentTime = Util.getMeasuringTimeMs();
        CursorAnimationState state = cursorStates.computeIfAbsent(cursor, c -> new CursorAnimationState(currentTime));
        state.lastFrameTime = currentTime;
        state.currentFrame = frameIndex;
    }

    public int getCurrentFrame(AnimatedCursor cursor) {
        long currentTime = Util.getMeasuringTimeMs();
        CursorAnimationState state = cursorStates.computeIfAbsent(cursor, c -> new CursorAnimationState(currentTime));

        if (!cursor.isAnimated()) {
            state.lastFrameTime = currentTime;
            state.currentFrame = 0;
            return 0;
        }

        if (currentTime - state.lastFrameTime >= cursor.getFrame(state.currentFrame).time() * 50L) { // 50ms = 1 tick
            state.lastFrameTime = currentTime;
            state.currentFrame = switch (cursor.getMode()) {
                case LOOP -> (state.currentFrame + 1) % cursor.getFrameCount();
                case HOLD -> Math.min(state.currentFrame + 1, cursor.getFrameCount() - 1);
                case OSCILLATE -> {
                    state.oscillateReverse = state.currentFrame != 0 && (state.currentFrame == cursor.getFrameCount() - 1 || state.oscillateReverse);
                    yield state.oscillateReverse ? state.currentFrame - 1 : state.currentFrame + 1;
                }
            };
        }
        return state.currentFrame;
    }
}