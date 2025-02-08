package io.github.fishstiz.minecraftcursor.config;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.cursor.CursorTypeRegistry;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CursorConfig {
    private String _hash;
    private boolean itemSlotEnabled = true;
    private boolean itemGrabbingEnabled = true;
    private boolean creativeTabsEnabled = true;
    private boolean enchantmentsEnabled = true;
    private boolean stonecutterRecipesEnabled = true;
    private boolean bookEditEnabled = true;
    private boolean loomPatternsEnabled = true;
    private boolean advancementTabsEnabled = true;
    private boolean worldIconEnabled = true;
    private boolean serverIconEnabled = true;
    protected Map<String, Settings> settings = new HashMap<>();
    File file;

    CursorConfig() {
        for (CursorType type : CursorTypeRegistry.types()) {
            this.getOrCreateCursorSettings(type);
        }
    }

    public Settings getOrCreateCursorSettings(CursorType type) {
        return settings.computeIfAbsent(type.getKey(), k -> new Settings());
    }

    public String get_hash() {
        if (this._hash == null) {
            generateHash();
        }

        return _hash;
    }

    private void generateHash() {
        long hash = 0;
        long prime = 31;

        for (Map.Entry<String, CursorConfig.Settings> entry : this.settings.entrySet()) {
            String key = entry.getKey();
            CursorConfig.Settings value = entry.getValue();

            for (char c : key.toCharArray()) {
                hash = hash * prime + c;
            }

            hash = hash * prime + (long) value.getScale();
            hash = hash * prime + value.getXHot();
            hash = hash * prime + value.getYHot();
            hash = hash * prime + (value.isEnabled() ? 1 : 0);
        }

        this.set_hash(Long.toHexString(hash));
    }

    public void set_hash(String hash) {
        _hash = hash;
    }

    public void save() {
        if (file == null) {
            throw new AssertionError("Cannot save config without file.");
        }

        CursorConfigLoader.saveConfig(file, this);
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

    public boolean isItemSlotEnabled() {
        return itemSlotEnabled;
    }

    public void setItemSlotEnabled(boolean itemSlotEnabled) {
        this.itemSlotEnabled = itemSlotEnabled;
    }

    public boolean isItemGrabbingEnabled() {
        return itemGrabbingEnabled;
    }

    public void setItemGrabbingEnabled(boolean itemGrabbingEnabled) {
        this.itemGrabbingEnabled = itemGrabbingEnabled;
    }

    public boolean isAdvancementTabsEnabled() {
        return advancementTabsEnabled;
    }

    public void setAdvancementTabsEnabled(boolean advancementTabsEnabled) {
        this.advancementTabsEnabled = advancementTabsEnabled;
    }

    public boolean isServerIconEnabled() {
        return serverIconEnabled;
    }

    public void setServerIconEnabled(boolean serverIconEnabled) {
        this.serverIconEnabled = serverIconEnabled;
    }

    public static class Settings {
        private double scale;
        private int xhot;
        private int yhot;
        private boolean enabled;

        public Settings() {
            this.scale = Default.SCALE;
            this.xhot = Default.X_HOT;
            this.yhot = Default.Y_HOT;
            this.enabled = Default.ENABLED;
        }

        public void update(double scale, int xhot, int yhot, boolean enabled) {
            this.scale = scale;
            this.xhot = xhot;
            this.yhot = yhot;
            this.enabled = enabled;
        }

        public double getScale() {
            double clampedScale = Math.clamp(this.scale, Default.SCALE_MIN, Default.SCALE_MAX);
            this.scale = Math.round(clampedScale / Default.SCALE_STEP) * Default.SCALE_STEP;
            return this.scale;
        }

        public int getXHot() {
            this.xhot = Math.clamp(this.xhot, Default.HOT_MIN, Default.HOT_MAX);
            return this.xhot;
        }

        public int getYHot() {
            this.yhot = Math.clamp(this.yhot, Default.HOT_MIN, Default.HOT_MAX);
            return this.yhot;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public static class Default {
            private Default() {
            }

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
    }
}
