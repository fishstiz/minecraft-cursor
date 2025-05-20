package io.github.fishstiz.minecraftcursor.config;

import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.util.SettingsUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.Objects;

import static io.github.fishstiz.minecraftcursor.util.SettingsUtil.sanitizeHotspot;
import static io.github.fishstiz.minecraftcursor.util.SettingsUtil.sanitizeScale;

public class Config extends AbstractConfig<Config.Settings> {
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
    private boolean remapCursorsEnabled = true;
    private final GlobalSettings global = new GlobalSettings();
    transient File file;

    Config() {
    }

    public Settings getOrCreateCursorSettings(Cursor cursor) {
        return settings.computeIfAbsent(cursor.getTypeKey(), k -> new Settings());
    }

    @Override
    public @NotNull String getHash() {
        if (this._hash == null) {
            this._hash = generateHash(this.settings);
        }
        return _hash;
    }

    public void setHash(String hash) {
        _hash = hash;
    }

    public void save() {
        ConfigLoader.saveConfig(Objects.requireNonNull(file), this);
    }

    public GlobalSettings getGlobal() {
        return global;
    }

    public void mergeResources(Resource resources) {
        for (Map.Entry<String, Config.Settings> entry : resources.getSettings().entrySet()) {
            Settings old = this.settings.computeIfAbsent(entry.getKey(), k -> new Settings());
            Settings validated = entry.getValue().copy();

            // preserve 'disabled' state of cursors
            // only allow external settings to disable
            if (!old.enabled) {
                validated.enabled = false;
            }

            this.settings.put(entry.getKey(), validated);
        }
    }

    public void layerResources(Resource resources) {
        for (Map.Entry<String, Config.Settings> entry : resources.getSettings().entrySet()) {
            this.settings.put(entry.getKey(), entry.getValue().copy());
        }
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

    public boolean isRemapCursorsEnabled() {
        return remapCursorsEnabled;
    }

    public void setRemapCursorsEnabled(boolean remapCursorsEnabled) {
        this.remapCursorsEnabled = remapCursorsEnabled;
    }

    private static String generateHash(Map<String, Settings> settings) {
        long hash = 0;
        long prime = 31;

        for (Map.Entry<String, Settings> entry : settings.entrySet()) {
            String key = entry.getKey();
            Settings value = entry.getValue();
            for (char c : key.toCharArray()) {
                hash = hash * prime + c;
            }
            hash = hash * prime + (long) value.scale;
            hash = hash * prime + value.xhot;
            hash = hash * prime + value.yhot;
            hash = hash * prime + (value.enabled ? 1 : 0);
        }

        return Long.toHexString(hash);
    }

    public static class Settings extends AbstractConfig.Settings<Settings> {
        protected boolean enabled = SettingsUtil.ENABLED;
        protected Boolean animated;

        Settings() {
        }

        public void update(Cursor cursor, double scale, int xhot, int yhot, boolean enabled) {
            Objects.requireNonNull(cursor);
            this.scale = sanitizeScale(scale);
            this.xhot = sanitizeHotspot(xhot, cursor);
            this.yhot = sanitizeHotspot(yhot, cursor);
            this.enabled = enabled;
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

        @Override
        public Settings copy() {
            Settings settings = new Settings();
            settings.scale = this.scale;
            settings.xhot = this.xhot;
            settings.yhot = this.yhot;
            settings.enabled = this.enabled;
            settings.animated = this.animated;
            return settings;
        }
    }

    public static class GlobalSettings extends AbstractConfig.Settings<GlobalSettings> {
        private boolean scaleActive = false;
        private boolean xhotActive = false;
        private boolean yhotActive = false;

        public void setActiveAll(boolean active) {
            setScaleActive(active);
            setXhotActive(active);
            setYhotActive(active);
        }

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
            this.scale = sanitizeScale(scale);
        }

        public void setXHotDouble(double xhot) {
            setXHot((int) xhot);
        }

        public void setXHot(int xhot) {
            this.xhot = SettingsUtil.sanitizeGlobalHotspot(xhot);
        }

        @Override
        public int getXHot() {
            return SettingsUtil.sanitizeGlobalHotspot(this.xhot);
        }

        public void setYHotDouble(double yhot) {
            setYHot((int) yhot);
        }

        public void setYHot(int yhot) {
            this.yhot = SettingsUtil.sanitizeGlobalHotspot(yhot);
        }

        @Override
        public int getYHot() {
            return SettingsUtil.sanitizeGlobalHotspot(this.yhot);
        }

        @Override
        GlobalSettings copy() {
            GlobalSettings globalSettings = new GlobalSettings();
            globalSettings.scale = this.scale;
            globalSettings.xhot = this.xhot;
            globalSettings.yhot = this.yhot;
            globalSettings.scaleActive = this.scaleActive;
            globalSettings.xhotActive = this.xhotActive;
            globalSettings.yhotActive = this.yhotActive;
            return globalSettings;
        }

        public <T extends AbstractConfig.Settings<T>> T apply(T settings) {
            T copied = settings.copy();
            copied.scale = this.isScaleActive() ? this.getScale() : copied.getScale();
            copied.xhot = this.isXHotActive() ? this.getXHot() : copied.getXHot();
            copied.yhot = this.isYHotActive() ? this.getYHot() : copied.getYHot();
            return copied;
        }
    }

    public static class Resource extends AbstractConfig<Settings> {
        @Override
        public @NotNull String getHash() {
            return Config.generateHash(this.settings);
        }

        public void layer(Map<String, Config.Settings> settings) {
            this.settings.putAll(settings);
        }
    }
}
