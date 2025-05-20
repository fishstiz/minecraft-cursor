package io.github.fishstiz.minecraftcursor.config;

import io.github.fishstiz.minecraftcursor.util.SettingsUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class Config<T extends Config.Settings<T>> implements Serializable {
    protected final Map<String, T> settings = new HashMap<>();

    abstract String getHash();

    public boolean isDifferent(Config<?> config) {
        return !this.getHash().equals(config.getHash());
    }

    public Map<String, T> getSettings() {
        return settings;
    }

    public abstract static class Settings<T extends Settings<T>> implements Serializable {
        protected double scale = SettingsUtil.SCALE;
        protected int xhot = SettingsUtil.X_HOT;
        protected int yhot = SettingsUtil.Y_HOT;

        protected Settings() {
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

        abstract T copy();
    }
}
