package io.github.fishstiz.minecraftcursor.util;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.config.Config;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

public class SettingsUtil {
    public static final int IMAGE_SIZE_MIN = 8;
    public static final int IMAGE_SIZE_MAX = 128;
    public static final int IMAGE_SIZE_STEP = 8;
    public static final double SCALE_AUTO_PREFERRED = 0;
    public static final double SCALE_AUTO_THRESHOLD_MAX = 0.49;
    public static final double SCALE = 1.0;
    public static final double SCALE_MIN = 0;
    public static final double SCALE_MAX = 8.0;
    public static final double SCALE_STEP = 0.05;
    public static final int X_HOT = 0;
    public static final int Y_HOT = 0;
    public static final int HOT_MIN = 0;
    public static final int HOT_STEP = 1;
    public static final int GLOBAL_HOT_MAX = IMAGE_SIZE_MAX - 1;
    public static final boolean ENABLED = true;

    private SettingsUtil() {
    }

    public static void assertImageSize(int imageWidth, int imageHeight) throws IOException {
        if (imageWidth < IMAGE_SIZE_MIN || imageWidth > IMAGE_SIZE_MAX || imageWidth % IMAGE_SIZE_STEP != 0) {
            throw new IOException("Unsupported image width: " + imageWidth);
        }
        if (imageHeight % imageWidth != 0) {
            throw new IOException("Image height must be divisible by width: " + imageHeight + " % " + imageWidth);
        }
    }

    public static boolean isAutoScale(double scale) {
        return scale <= SCALE_AUTO_THRESHOLD_MAX;
    }

    public static @Nullable Component getAutoText(double scale) {
        return isAutoScale(scale) ? Component.translatable("options.guiScale.auto") : null;
    }

    public static double getAutoScale(double scale) {
        if (isAutoScale(scale)) {
            OptionInstance<Integer> guiScale = Minecraft.getInstance().options.guiScale();
            int max = Integer.MAX_VALUE;
            int guiScaleValue = guiScale.get();

            if (guiScale.values() instanceof OptionInstance.ClampingLazyMaxIntRange range) {
                max = range.maxInclusive();
            }

            return guiScaleValue != 0 ? Math.min(max, guiScaleValue) : Minecraft.getInstance().getWindow().getGuiScale();
        }
        return scale;
    }

    public static double sanitizeScale(double scale) {
        double clampedScale = clamp(scale, SCALE_MIN, SCALE_MAX);
        double mappedScale = Math.round(clampedScale / SCALE_STEP) * SCALE_STEP;

        if (isAutoScale(mappedScale)) {
            return SCALE_AUTO_PREFERRED;
        }

        return (double) Math.round(mappedScale * 100) / 100;
    }

    public static int sanitizeHotspot(int hotspot, int imageWidth) {
        return clamp(hotspot, HOT_MIN, imageWidth - 1);
    }

    public static int sanitizeHotspot(int hotspot, @NotNull Cursor cursor) {
        return sanitizeHotspot(hotspot, cursor.isLoaded() ? cursor.getTextureWidth() : IMAGE_SIZE_MAX);
    }

    public static int sanitizeHotspot(double hotspot, @NotNull Cursor cursor) {
        return sanitizeHotspot((int) hotspot, cursor);
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

    public static int getMaxHotspot(Cursor cursor) {
        if (cursor != null && cursor.isLoaded()) {
            return cursor.getTextureWidth() - 1;
        }
        return GLOBAL_HOT_MAX;
    }

    public static int getMaxHotspot(Collection<Cursor> cursors) {
        int max = -1;
        for (Cursor cursor : cursors) {
            if (cursor.isLoaded()) {
                int maxHotspot = getMaxHotspot(cursor);
                if (maxHotspot > max) {
                    max = maxHotspot;
                }
            }
        }
        return max != -1 ? max : GLOBAL_HOT_MAX;
    }

    public static boolean equalSettings(@Nullable Config.Settings a, @Nullable Config.Settings b, boolean excludeGlobal) {
        if (Objects.equals(a, b)) {
            return true;
        }
        if (a != null && b != null) {
            boolean equal = a.isEnabled() == b.isEnabled() &&
                            Objects.equals(a.isAnimated(), b.isAnimated());

            if (!excludeGlobal || !MinecraftCursor.CONFIG.getGlobal().isXHotActive()) {
                equal &= a.getXHot() == b.getXHot();
            }
            if (!excludeGlobal || !MinecraftCursor.CONFIG.getGlobal().isYHotActive()) {
                equal &= a.getYHot() == b.getYHot();
            }
            if (!excludeGlobal || !MinecraftCursor.CONFIG.getGlobal().isScaleActive()) {
                equal &= Double.compare(a.getScale(), b.getScale()) == 0;
            }

            return equal;
        }
        return false;
    }

    public static <E> void forEach(E[] arr, Consumer<E> action) {
        for (E e : arr) {
            action.accept(e);
        }
    }
}
