package io.github.fishstiz.minecraftcursor.config;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;

import java.util.HashMap;
import java.util.Map;

public class CursorConfig {
    @JsonProperty
    protected final Map<String, Settings> settings = new HashMap<>();
    @JsonProperty
    private String _hash;

    protected void createCursorSettings(CursorType type) {
        settings.put(type.getKey(), Settings.create(Defaults.SCALE, Defaults.X_HOT, Defaults.Y_HOT, Defaults.ENABLED));
    }

    public Settings getCursorSettings(CursorType type) {
        return settings.computeIfAbsent(type.getKey(), k -> Settings.create(Defaults.SCALE, Defaults.X_HOT, Defaults.Y_HOT, Defaults.ENABLED));
    }

    protected void updateCursorSettings(CursorType type, Settings settings) {
        this.settings.get(type.getKey()).update(settings.getScale(), settings.getXHot(), settings.getYHot(), settings.getEnabled());
    }

    @JsonGetter("_hash")
    public String get_hash() {
        return _hash;
    }

    public void set_hash(String hash) {
        _hash = hash;
    }

    public static class Defaults {
        public static final double SCALE = 1.0;
        public static final double SCALE_MIN = 1.0;
        public static final double SCALE_MAX = 3.0;
        public static final double SCALE_STEP = 0.25;
        public static final int X_HOT = 0;
        public static final int Y_HOT = 0;
        public static final int HOT_MIN = 0;
        public static final int HOT_MAX = 31;
        public static final boolean ENABLED = true;
    }

    public static class Settings {
        private double scale;
        private int xhot;
        private int yhot;
        private boolean enabled;

        public Settings() {
            this.scale = Defaults.SCALE;
            this.xhot = Defaults.X_HOT;
            this.yhot = Defaults.Y_HOT;
            this.enabled = Defaults.ENABLED;
        }

        public void update(double scale, int xhot, int yhot, boolean enabled) {
            this.scale = scale;
            this.xhot = xhot;
            this.yhot = yhot;
            this.enabled = enabled;
        }

        public double getScale() {
            double scale = Math.round(this.scale * Defaults.SCALE_STEP) / Defaults.SCALE_STEP;
            scale = Math.max(Defaults.SCALE_MIN, Math.min(Defaults.SCALE_MAX, scale));
            return scale;
        }

        public int getXHot() {
            return Math.max(Defaults.HOT_MIN, Math.min(Defaults.HOT_MAX, this.xhot));
        }

        public int getYHot() {
            return Math.max(Defaults.HOT_MIN, Math.min(Defaults.HOT_MAX, this.yhot));
        }

        public boolean getEnabled() {
            return enabled;
        }

        public static Settings create(double scale, int xhot, int yhot, boolean enabled) {
            Settings settings = new Settings();
            settings.scale = scale;
            settings.xhot = xhot;
            settings.yhot = yhot;
            settings.enabled = enabled;
            return settings;
        }
    }
}




