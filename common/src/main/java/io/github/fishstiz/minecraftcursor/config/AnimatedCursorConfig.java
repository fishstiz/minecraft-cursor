package io.github.fishstiz.minecraftcursor.config;

import com.google.gson.*;
import io.github.fishstiz.minecraftcursor.cursor.AnimationMode;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class AnimatedCursorConfig implements Serializable {
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

    public static class Frame implements Serializable {
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
    }

    public static class FrameDeserializer implements JsonDeserializer<Frame> {
        @Override
        public Frame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
                return new Frame(json.getAsInt(), 0);
            } else if (json.isJsonObject()) {
                JsonObject obj = json.getAsJsonObject();
                int index = obj.has("index") ? obj.get("index").getAsInt() : 0;
                int time = obj.has("time") ? obj.get("time").getAsInt() : 0;
                return new Frame(index, time);
            }
            throw new JsonParseException("Invalid Frame format");
        }
    }
}
