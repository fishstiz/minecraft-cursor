package io.github.fishstiz.minecraftcursor.config;

import io.github.fishstiz.minecraftcursor.cursor.AnimationMode;

import java.util.ArrayList;
import java.util.List;

public class AnimatedCursorConfig {
    private static final int MIN_TIME = 1;
    public final AnimationMode mode;
    public final int frametime;
    public final List<Frame> frames = new ArrayList<>();

    public AnimatedCursorConfig() {
        this.mode = AnimationMode.LOOP;
        this.frametime = MIN_TIME;
    }

    public int getTime(int index) {
        for (Frame frame : frames) {
            if (frame.index == index) {
                return frame.time > 0 ? frame.time : Math.max(frametime, MIN_TIME);
            }
        }
        return Math.max(frametime, MIN_TIME);
    }

    public static class Frame {
        public final int index;
        public final int time;

        public Frame() {
            this.index = 0;
            this.time = MIN_TIME;
        }
    }
}
