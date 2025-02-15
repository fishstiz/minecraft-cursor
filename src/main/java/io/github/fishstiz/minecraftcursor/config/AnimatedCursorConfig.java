package io.github.fishstiz.minecraftcursor.config;

import java.util.List;

public record AnimatedCursorConfig(int frametime, List<Frame> frames) {
    private static final int MIN_TIME = 1;

    public AnimatedCursorConfig {
        frametime = Math.max(frametime, MIN_TIME);
        frames = List.copyOf(frames);
    }

    public int getTime(int index) {
        for (Frame frame : frames) {
            if (frame.index == index) {
                return frame.time;
            }
        }
        return frametime;
    }

    public record Frame(int index, int time) {
        public Frame {
            time = Math.max(time, MIN_TIME);
        }
    }
}
