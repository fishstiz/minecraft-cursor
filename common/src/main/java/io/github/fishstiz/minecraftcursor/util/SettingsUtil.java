package io.github.fishstiz.minecraftcursor.util;

import static io.github.fishstiz.minecraftcursor.config.CursorConfig.Settings.Default.*;

public class SettingsUtil {
    private SettingsUtil() {
    }

    public static double sanitizeScale(double scale) {
        double clampedScale = clamp(scale, SCALE_MIN, SCALE_MAX);
        double mappedScale = Math.round(clampedScale / SCALE_STEP) * SCALE_STEP;
        return (double) Math.round(mappedScale * 100) / 100;
    }

    public static int sanitizeHotspot(int hotspot) {
        return clamp(hotspot, HOT_MIN, HOT_MAX);
    }


    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
