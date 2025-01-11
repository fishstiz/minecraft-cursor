package io.github.fishstiz.minecraftcursor.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;

import java.util.HashMap;
import java.util.Map;

public class CursorConfig {
    @JsonProperty
    private final Map<String, Settings> settings = new HashMap<>();

    protected void createCursorSettings(CursorType type) {
        settings.put(type.getKey(), new Settings(Defaults.SCALE, Defaults.X_HOT, Defaults.Y_HOT, Defaults.ENABLED));
    }

    protected Settings getCursorSettings(CursorType type) {
        return settings.computeIfAbsent(type.getKey(), k -> new Settings(Defaults.SCALE, Defaults.X_HOT, Defaults.Y_HOT, Defaults.ENABLED));
    }

    protected void updateCursorSettings(CursorType type, Settings settings) {
        this.settings.get(type.getKey()).update(settings.scale, settings.xhot, settings.yhot, settings.enabled);
    }

    public static class Defaults {
        public static final double SCALE = 1.0;
        public static final int X_HOT = 0;
        public static final int Y_HOT = 0;
        public static final boolean ENABLED = true;
    }

    public static class Settings {
        private double scale;
        private int xhot;
        private int yhot;
        private boolean enabled;

        public Settings(
                @JsonProperty("scale") double scale,
                @JsonProperty("xhot") int xhot,
                @JsonProperty("yhot") int yhot,
                @JsonProperty("enabled") boolean enabled) {
            update(scale, xhot, yhot, enabled);
        }

        public void update(double scale, int xhot, int yhot, boolean enabled) {
            this.scale = scale;
            this.xhot = xhot;
            this.yhot = yhot;
            this.enabled = enabled;
        }

        public double getScale() {
            return scale;
        }

        public int getXHot() {
            return xhot;
        }

        public int getYHot() {
            return yhot;
        }

        public boolean getEnabled() {
            return enabled;
        }
    }
}




