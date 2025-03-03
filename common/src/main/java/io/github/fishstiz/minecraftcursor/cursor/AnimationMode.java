package io.github.fishstiz.minecraftcursor.cursor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;

public enum AnimationMode {
    @JsonEnumDefaultValue
    LOOP,
    LOOP_REVERSE,
    FORWARDS,
    REVERSE,
    OSCILLATE,
    RANDOM,
    RANDOM_CYCLE;

    @JsonValue
    public String toLowerCase() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static AnimationMode getOrDefault(String name) {
        try {
            return AnimationMode.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            MinecraftCursor.LOGGER.warn("Animation mode: '{}' does not exist. Using default 'loop'.", name);
            return AnimationMode.LOOP;
        }
    }
}
