package io.github.fishstiz.minecraftcursor.config;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;

import java.util.HashMap;
import java.util.Map;

public class CursorConfig {
    @JsonProperty
    private String _hash;
    private boolean creativeTabsEnabled = true;
    private boolean enchantmentsEnabled = true;
    private boolean stonecutterRecipesEnabled = true;
    private boolean bookEditEnabled = true;
    private boolean loomPatternsEnabled = true;
    private boolean worldIconEnabled = true;

    @JsonProperty
    protected Map<String, Settings> settings = new HashMap<>();

    public Settings getOrCreateCursorSettings(CursorType type) {
        return settings.computeIfAbsent(type.getKey(), k -> new Settings());
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

    public Map<String, Settings> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Settings> settings) {
        this.settings = settings;
    }

    public boolean isCreativeTabsEnabled() {
        return creativeTabsEnabled;
    }

    public void setCreativeTabsEnabled(boolean creativeTabsEnabled) {
        this.creativeTabsEnabled = creativeTabsEnabled;
    }

    public boolean isEnchantmentsEnabled() {
        return enchantmentsEnabled;
    }

    public void setEnchantmentsEnabled(boolean enchantmentsEnabled) {
        this.enchantmentsEnabled = enchantmentsEnabled;
    }

    public boolean isStonecutterRecipesEnabled() {
        return stonecutterRecipesEnabled;
    }

    public void setStonecutterRecipesEnabled(boolean stonecutterRecipesEnabled) {
        this.stonecutterRecipesEnabled = stonecutterRecipesEnabled;
    }

    public boolean isBookEditEnabled() {
        return bookEditEnabled;
    }

    public void setBookEditEnabled(boolean bookEditEnabled) {
        this.bookEditEnabled = bookEditEnabled;
    }

    public boolean isLoomPatternsEnabled() {
        return loomPatternsEnabled;
    }

    public void setLoomPatternsEnabled(boolean loomPatternsEnabled) {
        this.loomPatternsEnabled = loomPatternsEnabled;
    }

    public boolean isWorldIconEnabled() {
        return worldIconEnabled;
    }

    public void setWorldIconEnabled(boolean worldIconEnabled) {
        this.worldIconEnabled = worldIconEnabled;
    }

    public static class Defaults {
        public static final double SCALE = 1.0;
        public static final double SCALE_MIN = 0.5;
        public static final double SCALE_MAX = 3.0;
        public static final double SCALE_STEP = 0.05;
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
            double scale = Math.max(Defaults.SCALE_MIN, Math.min(Defaults.SCALE_MAX, this.scale));
            this.scale = Math.round(scale / Defaults.SCALE_STEP) * Defaults.SCALE_STEP;
            return this.scale;
        }

        public int getXHot() {
            xhot = Math.max(Defaults.HOT_MIN, Math.min(Defaults.HOT_MAX, this.xhot));
            return xhot;
        }

        public int getYHot() {
            yhot = Math.max(Defaults.HOT_MIN, Math.min(Defaults.HOT_MAX, this.yhot));
            return yhot;
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




