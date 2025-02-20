package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;

public class CursorOptionsHandler {
    private static final int SCALE_OVERRIDE = -1;
    private final CursorOptionsWidget options;

    CursorOptionsHandler(CursorOptionsWidget optionsWidget) {
        this.options = optionsWidget;
    }

    void handleEnable(boolean enabled) {
        Cursor cursor = getCursor();

        cursor.enable(enabled);
        updateSettings();

        if (cursor instanceof AnimatedCursor) {
            options.resetAnimation.active = options.animateButton.value && enabled;
        }
    }

    Consumer<Double> handleChangeHotspots(DoubleConsumer handleChangeHotspot) {
        return value -> {
            handleChangeHotspot.accept(value);
            options.cursorHotspot.setRulerRendered(true, true);
        };
    }

    void handlePressAnimate(boolean animated) {
        getCursorAsAnimatedCursor().ifPresent(animatedCursor -> {
            animatedCursor.setAnimated(animated);
            getSettings().setAnimated(animatedCursor.isAnimated());
            options.resetAnimation.active = options.enableButton.value && animated;
        });

        if (!animated) options.cursorHotspot.setRulerRendered(true, true);
    }

    void handleResetAnimation() {
        getCursorAsAnimatedCursor().ifPresent(animatedCursor -> options.parent().animationHelper.reset(animatedCursor));

        options.cursorHotspot.setRulerRendered(false, false);
    }

    void handleChangeScale(double scale) {
        Cursor cursor = getCursor();

        if (cursor.getScale() != scale) {
            CursorController.getInstance().overrideCursor(cursor.getType(), SCALE_OVERRIDE);
        }

        cursor.setScale(scale);
        updateSettings();
    }

    void handleChangeXHot(double xhot) {
        getCursor().setXHot((int) xhot);
        updateSettings();
    }

    void handleChangeYHot(double yhot) {
        getCursor().setYHot((int) yhot);
        updateSettings();
    }

    Cursor getCursor() {
        return options.parent().getSelectedCursor();
    }

    Optional<AnimatedCursor> getCursorAsAnimatedCursor() {
        return getCursor() instanceof AnimatedCursor animatedCursor
                ? Optional.of(animatedCursor)
                : Optional.empty();
    }

    boolean isAnimated() {
        return getCursorAsAnimatedCursor().map(AnimatedCursor::isAnimated).orElse(false);
    }

    CursorConfig.Settings getSettings() {
        return CONFIG.getOrCreateCursorSettings(getCursor().getType());
    }

    public void updateSettings() {
        CursorConfig.GlobalSettings global = CONFIG.getGlobal();
        CursorConfig.Settings settings = getSettings();
        Cursor cursor = getCursor();

        getSettings().update(
                global.isScaleActive() ? settings.getScale() : cursor.getScale(),
                global.isXHotActive() ? settings.getXHot() : cursor.getXHot(),
                global.isYHotActive() ? settings.getYHot() : cursor.getYHot(),
                cursor.isEnabled()
        );
    }

    public static void removeScaleOverride() {
        CursorController.getInstance().removeOverride(SCALE_OVERRIDE);
    }
}
