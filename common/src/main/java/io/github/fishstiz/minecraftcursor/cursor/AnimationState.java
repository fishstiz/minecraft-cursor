package io.github.fishstiz.minecraftcursor.cursor;

import net.minecraft.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnimationState {
    private static final long MS_PER_TICK = 50;
    private static final Random RANDOM = new Random();
    private long lastFrameTime = 0;
    private int currentFrame = 0;
    private boolean oscillateReversed = false;
    private List<Integer> remainingFrames;

    private boolean isNext(AnimatedCursor cursor, long currentTime) {
        return currentTime - this.lastFrameTime >= cursor.getFrame(this.currentFrame).time() * MS_PER_TICK;
    }

    public int next(AnimatedCursor cursor) {
        long currentTime = Util.getMillis();
        if (!this.isNext(cursor, currentTime)) {
            return this.currentFrame;
        }

        this.lastFrameTime = currentTime;
        this.currentFrame = switch (cursor.getMode()) {
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
        lastFrameTime = Util.getMillis();
        currentFrame = 0;
        oscillateReversed = false;
        remainingFrames = null;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }
}
