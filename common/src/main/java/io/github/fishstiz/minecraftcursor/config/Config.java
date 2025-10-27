package io.github.fishstiz.minecraftcursor.config;

import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.util.SettingsUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
    private boolean loomPatternsEnabled = true;
    private boolean advancementTabsEnabled = true;
    private boolean worldIconEnabled = true;
    private boolean serverIconEnabled = true;
    private boolean remapCursorsEnabled = true;
    private boolean deferredLoading = false;
    private boolean inactiveWidgetsEnabled = true;
    private boolean aggressiveCursor = false;
    private boolean virtualMode = false;
    private final List<String> blacklist = new ArrayList<>();
    private final GlobalSettings global = new GlobalSettings();
    transient File file;

    Config() {
    }

    public Settings getOrCreateSettings(Cursor cursor) {
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
        ConfigLoader.save(Objects.requireNonNull(file), this);
    }

    public GlobalSettings getGlobal() {
        return global;
    }

    public List<String> getBlacklist() {
        return this.blacklist;
    }

    private Settings validateSettings(String key, Settings settings) {
        Settings old = this.settings.computeIfAbsent(key, k -> new Settings());
        Settings validated = settings.copy();

        // resource packs can only disable
        if (!old.enabled) {
            validated.enabled = false;
        }

        return validated;
    }

    private Config.Settings filterInactive(@NotNull Cursor cursor, @NotNull Config.Settings settingsToApply) {
        Config.Settings currentSettings = this.settings.computeIfAbsent(cursor.getTypeKey(), k -> new Config.Settings());
        Config.Settings validated = settingsToApply.copy();

        if (this.global.isScaleActive()) {
            validated.setScale(currentSettings.getScale());
        }
        if (this.global.isXHotActive()) {
            validated.setXHot(cursor, currentSettings.getXHot());
        }
        if (this.global.isYHotActive()) {
            validated.setYHot(cursor, currentSettings.getYHot());
        }
        return validated;
    }

    public void replaceActiveSettings(Resource resource, Cursor cursor) {
        this.settings.put(cursor.getTypeKey(), this.filterInactive(cursor, resource.getOrCreateSettings(cursor)));
    }

    public void merge(Resource resources) {
        for (Map.Entry<String, Config.Settings> resourceEntry : resources.getAllSettings().entrySet()) {
            String key = resourceEntry.getKey();
            this.settings.put(key, this.validateSettings(key, resourceEntry.getValue()));
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

    public boolean isDeferredLoading() {
        return deferredLoading;
    }

    public void setDeferredLoading(boolean deferredLoading) {
        this.deferredLoading = deferredLoading;
    }

    public boolean isInactiveWidgetsEnabled() {
        return inactiveWidgetsEnabled;
    }

    public void setInactiveWidgetsEnabled(boolean inactiveWidgetsEnabled) {
        this.inactiveWidgetsEnabled = inactiveWidgetsEnabled;
    }

    public boolean isAggressiveCursor() {
        return aggressiveCursor;
    }

    public void setAggressiveCursor(boolean aggressiveCursor) {
        this.aggressiveCursor = aggressiveCursor;
    }

    public boolean isVirtualMode() {
        return virtualMode;
    }

    public void setVirtualMode(boolean virtualMode) {
        this.virtualMode = virtualMode;
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

        public Settings() {
        }

        public Settings(double scale, int xhot, int yhot, boolean enabled, Boolean animated) {
            this.scale = scale;
            this.xhot = xhot;
            this.yhot = yhot;
            this.enabled = enabled;
            this.animated = animated;
        }

        public void setScale(double scale) {
            this.scale = sanitizeScale(scale);
        }

        public void setXHot(@NotNull Cursor cursor, int xhot) {
            this.xhot = sanitizeHotspot(xhot, cursor);
        }

        public void setYHot(@NotNull Cursor cursor, int yhot) {
            this.yhot = sanitizeHotspot(yhot, cursor);
        }

        public void setEnabled(boolean enabled) {
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
            return new Settings(this.scale, this.xhot, this.yhot, this.enabled, this.animated);
        }
    }

    public static class GlobalSettings extends AbstractConfig.Settings<GlobalSettings> {
        private boolean scaleActive = false;
        private boolean xhotActive = false;
        private boolean yhotActive = false;

        public void setActiveAll(boolean active) {
            setScaleActive(active);
            setXHotActive(active);
            setYHotActive(active);
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

        public void setXHotActive(boolean xhotActive) {
            this.xhotActive = xhotActive;
        }

        public boolean isYHotActive() {
            return yhotActive;
        }

        public void setYHotActive(boolean yhotActive) {
            this.yhotActive = yhotActive;
        }

        public void setScale(double scale) {
            this.scale = sanitizeScale(scale);
        }

        public void setXHot(double xhot) {
            setXHot((int) xhot);
        }

        public void setXHot(int xhot) {
            this.xhot = SettingsUtil.sanitizeGlobalHotspot(xhot);
        }

        @Override
        public int getXHot() {
            return SettingsUtil.sanitizeGlobalHotspot(this.xhot);
        }

        public void setYHot(double yhot) {
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
        public Config.Settings getOrCreateSettings(Cursor cursor) {
            return settings.computeIfAbsent(cursor.getTypeKey(), k -> new Config.Settings());
        }

        @Override
        public @NotNull String getHash() {
            return Config.generateHash(this.settings);
        }

        public void layer(Map<String, Config.Settings> settings) {
            this.settings.putAll(settings);
        }
    }
}
