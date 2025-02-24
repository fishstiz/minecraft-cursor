package io.github.fishstiz.minecraftcursor.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.util.SettingsUtil;

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
    private final GlobalSettings global = new GlobalSettings();
    private Map<String, Settings> settings = new HashMap<>();
    File file;

    CursorConfig() {
        for (CursorType type : CursorManager.INSTANCE.getCursorTypes()) {
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

        for (Map.Entry<String, Settings> entry : this.settings.entrySet()) {
            String key = entry.getKey();
            Settings value = entry.getValue();

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
            throw new NullPointerException("Cannot save config when initialized without file.");
        }
        CursorConfigLoader.saveConfig(file, this);
    }

    public GlobalSettings getGlobal() {
        return global;
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
        protected double scale = Default.SCALE;
        protected int xhot = Default.X_HOT;
        protected int yhot = Default.Y_HOT;
        private boolean enabled = Default.ENABLED;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean animated;

        public void update(double scale, int xhot, int yhot, boolean enabled) {
            this.scale = SettingsUtil.sanitizeScale(scale);
            this.xhot = SettingsUtil.sanitizeHotspot(xhot);
            this.yhot = SettingsUtil.sanitizeHotspot(yhot);
            this.enabled = enabled;
        }

        public double getScale() {
            return SettingsUtil.sanitizeScale(this.scale);
        }

        public int getXHot() {
            return SettingsUtil.sanitizeHotspot(this.xhot);
        }

        public int getYHot() {
            return SettingsUtil.sanitizeHotspot(this.yhot);
        }

        public boolean isEnabled() {
            return enabled;
        }

        public Boolean isAnimated() {
            return this.animated;
        }

        public void setAnimated(boolean animated) {
            this.animated = animated;
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

    public static class GlobalSettings extends Settings {
        private boolean scaleActive = false;
        private boolean xhotActive = false;
        private boolean yhotActive = false;

        public boolean isScaleActive() {
            return scaleActive;
        }

        public void setScaleActive(boolean scaleEnabled) {
            this.scaleActive = scaleEnabled;
        }

        public boolean isXHotActive() {
            return xhotActive;
        }

        public void setXhotActive(boolean xhotActive) {
            this.xhotActive = xhotActive;
        }

        public boolean isYHotActive() {
            return yhotActive;
        }

        public void setYhotActive(boolean yhotActive) {
            this.yhotActive = yhotActive;
        }

        public void setScale(double scale) {
            this.scale = SettingsUtil.sanitizeScale(scale);
        }

        public void setXHotDouble(double xhot) {
            setXHot((int) xhot);
        }

        public void setXHot(int xhot) {
            this.xhot = SettingsUtil.sanitizeHotspot(xhot);
        }

        public void setYHotDouble(double yhot) {
            setYHot((int) yhot);
        }

        public void setYHot(int yhot) {
            this.yhot = SettingsUtil.sanitizeHotspot(yhot);
        }
    }
}
