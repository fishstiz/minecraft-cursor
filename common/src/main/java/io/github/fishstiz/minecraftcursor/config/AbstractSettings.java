package io.github.fishstiz.minecraftcursor.config;

import io.github.fishstiz.minecraftcursor.util.SettingsUtil;

import java.io.Serializable;

public abstract class AbstractSettings<T extends AbstractSettings<T>> implements Serializable {
    protected double scale = SettingsUtil.SCALE;
    protected int xhot = SettingsUtil.X_HOT;
    protected int yhot = SettingsUtil.Y_HOT;

    protected AbstractSettings() {
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
