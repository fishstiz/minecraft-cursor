package io.github.fishstiz.minecraftcursor.config;

import java.util.ArrayList;
import java.util.List;

public class AnimatedCursorConfig {
    private static final int MIN_TIME = 1;
    public final int frametime;
    public final List<Frame> frames = new ArrayList<>();

    public AnimatedCursorConfig() {
        this.frametime = MIN_TIME;
    }

    public int getTime(int index) {
        for (Frame frame : frames) {
            if (frame.index == index) {
                return frame.time;
            }
        }
        return frametime;
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
