package io.github.fishstiz.minecraftcursor.util;

import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class SettingsUtil {
    public static final Set<Integer> SUPPORTED_SIZES = Set.of(8, 16, 32, 48, 64);
    public static final double SCALE_AUTO_THRESHOLD_MAX = 0.49;
    public static final double SCALE = 1.0;
    public static final double SCALE_MIN = 0;
    public static final double SCALE_MAX = 8.0;
    public static final double SCALE_STEP = 0.05;
    public static final int X_HOT = 0;
    public static final int Y_HOT = 0;
    public static final int HOT_MIN = 0;
    public static final int GLOBAL_HOT_MAX = Collections.max(SUPPORTED_SIZES) - 1;
    public static final boolean ENABLED = true;

    private SettingsUtil() {
    }

    public static boolean isAutoScale(double scale) {
        return scale <= SCALE_AUTO_THRESHOLD_MAX;
    }

    public static Component getAutoText(double scale) {
        return isAutoScale(scale) ? Component.translatable("options.guiScale.auto") : null;
    }

    public static double getAutoScale(double scale) {
        return isAutoScale(scale) ? Minecraft.getInstance().getWindow().getGuiScale() : scale;
    }

    public static double sanitizeScale(double scale) {
        double clampedScale = clamp(scale, SCALE_MIN, SCALE_MAX);
        double mappedScale = Math.round(clampedScale / SCALE_STEP) * SCALE_STEP;

        if (isAutoScale(mappedScale)) {
            return 0;
        }

        return (double) Math.round(mappedScale * 100) / 100;
    }

    public static int sanitizeHotspot(int hotspot, Cursor cursor) {
        return clamp(hotspot, HOT_MIN, cursor.isLoaded() ? Objects.requireNonNull(cursor).getTextureWidth() - 1 : GLOBAL_HOT_MAX);
    }

    public static int sanitizeGlobalHotspot(int hotspot) {
        return clamp(hotspot, HOT_MIN, GLOBAL_HOT_MAX);
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int getMaxHotspot(Collection<Cursor> cursors) {
        int max = SUPPORTED_SIZES.iterator().next();
        for (Cursor cursor : cursors) {
            if (cursor.isLoaded()) {
                int textureWidth = cursor.getTextureWidth();
                if (textureWidth > max) {
                    max = textureWidth;
                }
            }
        }
        return max;
    }
}
