package io.github.fishstiz.minecraftcursor.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.fishstiz.minecraftcursor.cursor.AnimationMode;

import java.util.List;

public class AnimatedCursorConfig {
    private static final int MIN_TIME = 1;
    public final AnimationMode mode;
    private final int frametime;
    private final List<Frame> frames = List.of();

    public AnimatedCursorConfig() {
        this.mode = AnimationMode.LOOP;
        this.frametime = MIN_TIME;
    }

    public int getFrametime() {
        return Math.max(this.frametime, MIN_TIME);
    }

    public List<Frame> getFrames() {
        return List.copyOf(this.frames);
    }

    public static class Frame {
        private final int index;
        private int time;

        public Frame(int index, int time) {
            this.index = index;
            this.time = time;
        }

        public int getIndex() {
            return this.index;
        }

        public int getTime(AnimatedCursorConfig config) {
            this.time = this.time > 0 ? this.time : Math.max(config.getFrametime(), MIN_TIME);
            return this.time;
        }

        @JsonCreator
        public static Frame setFrames(JsonNode node) {
            if (node.isInt()) {
                return new Frame(node.asInt(), 0);
            } else {
                ObjectNode obj = (ObjectNode) node;
                int index = obj.has("index") ? obj.get("index").asInt() : 0;
                int time = obj.has("time") ? obj.get("time").asInt() : 0;
                return new Frame(index, time);
            }
        }
    }
}
