package io.github.fishstiz.minecraftcursor.cursor;

import net.minecraft.Util;

import java.util.Random;

public class AnimationState {
    private static final long MS_PER_TICK = 50;
    private static final Random RANDOM = new Random();
    private long lastFrameTime = 0;
    private int currentFrame = 0;
    private boolean oscillateReversed = false;
    private int[] shuffledFrames;
    private int shuffledIndex = 0;

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
                if (cursor.getFrameCount() > 1) {
                    int newFrame;
                    do newFrame = RANDOM.nextInt(cursor.getFrameCount());
                    while (newFrame == currentFrame);
                    yield newFrame;
                }
                yield currentFrame;
            }
            case RANDOM_CYCLE -> {
                if (shuffledFrames == null || shuffledIndex >= shuffledFrames.length) {
                    int count = cursor.getFrameCount();
                    shuffledFrames = new int[count];
                    for (int i = 0; i < count; i++) {
                        shuffledFrames[i] = i;
                    }
                    for (int i = count - 1; i > 0; i--) {
                        int j = RANDOM.nextInt(i + 1);
                        int tmp = shuffledFrames[i];
                        shuffledFrames[i] = shuffledFrames[j];
                        shuffledFrames[j] = tmp;
                    }
                    shuffledIndex = 0;
                }
                yield shuffledFrames[shuffledIndex++];
            }
        };
        return currentFrame;
    }

    public void reset() {
        lastFrameTime = Util.getMillis();
        currentFrame = 0;
        oscillateReversed = false;
        shuffledFrames = null;
        shuffledIndex = 0;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }
}
