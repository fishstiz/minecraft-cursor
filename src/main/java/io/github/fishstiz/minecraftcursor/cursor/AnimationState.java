package io.github.fishstiz.minecraftcursor.cursor;

import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnimationState {
    private static final Random RANDOM = new Random();
    private long lastFrameTime = Util.getMeasuringTimeMs();
    private int currentFrame = 0;
    private boolean oscillateReversed = false;
    private List<Integer> remainingFrames;

    public boolean nextFrame(AnimatedCursor cursor) {
        long currentTime = Util.getMeasuringTimeMs();
        boolean isNextFrame = currentTime - lastFrameTime >= cursor.getFrame(currentFrame).time() * 50L; // 50ms = 1 tick
        if (isNextFrame) next(cursor, currentTime);
        return isNextFrame;
    }

    public int next(AnimatedCursor cursor, long currentTime) {
        lastFrameTime = currentTime;

        currentFrame = switch (cursor.getMode()) {
            case LOOP, LOOP_REVERSE -> (currentFrame + 1) % cursor.getFrameCount();
            case FORWARDS, REVERSE -> Math.min(currentFrame + 1, cursor.getFrameCount() - 1);
            case OSCILLATE -> {
                oscillateReversed = currentFrame != 0 && (currentFrame == cursor.getFrameCount() - 1 || oscillateReversed);
                yield oscillateReversed ? currentFrame - 1 : currentFrame + 1;
            }
            case RANDOM -> {
                int newFrame;
                do newFrame = RANDOM.nextInt(cursor.getFrameCount());
                while (newFrame == currentFrame);
                yield newFrame;
            }
            case RANDOM_CYCLE -> {
                if (remainingFrames == null || remainingFrames.isEmpty()) {
                    remainingFrames = new ArrayList<>(cursor.getFrameCount());
                    for (int i = 0; i < cursor.getFrameCount(); i++) {
                        remainingFrames.add(i);
                    }
                }
                yield remainingFrames.remove(RANDOM.nextInt(remainingFrames.size()));
            }
        };

        return currentFrame;
    }

    public void reset() {
        lastFrameTime = Util.getMeasuringTimeMs();
        currentFrame = 0;
        oscillateReversed = false;
        remainingFrames = null;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }
}
