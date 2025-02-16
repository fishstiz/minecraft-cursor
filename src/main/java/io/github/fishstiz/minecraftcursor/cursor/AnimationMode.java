package io.github.fishstiz.minecraftcursor.cursor;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;

import java.lang.reflect.Type;

public enum AnimationMode {
    LOOP,
    LOOP_REVERSE,
    FORWARDS,
    REVERSE,
    OSCILLATE,
    RANDOM,
    RANDOM_LOOP;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public static AnimationMode getOrDefault(String name) {
        try {
            return AnimationMode.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            MinecraftCursor.LOGGER.warn("Animation mode: '{}' does not exist. Using default 'loop'.", name);
            return AnimationMode.LOOP;
        }
    }

    public static class Deserializer implements JsonDeserializer<AnimationMode> {
        @Override
        public AnimationMode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return AnimationMode.getOrDefault(json.getAsString());
        }
    }
}
