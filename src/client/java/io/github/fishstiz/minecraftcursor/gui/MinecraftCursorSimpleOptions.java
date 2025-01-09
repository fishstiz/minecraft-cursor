package io.github.fishstiz.minecraftcursor.gui;

import io.github.fishstiz.minecraftcursor.MinecraftCursorHandler;
import io.github.fishstiz.minecraftcursor.config.MinecraftCursorConfigDefinition;
import io.github.fishstiz.minecraftcursor.config.MinecraftCursorConfigManager;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class MinecraftCursorSimpleOptions {
    private final MinecraftCursorHandler cursorHandler;
    private final MinecraftCursorHotspot cursorHotspot;
    private SimpleOption<Boolean> enabled;
    private static final double SCALE_MIN_VALUE = 0.25;
    private static final double SCALE_MAX_VALUE = 3.0;
    private static final double SCALE_STEP = 0.25;
    private SimpleOption<Double> scale;
    private SimpleOption<Integer> xhot;
    private SimpleOption<Integer> yhot;
    private SimpleOption<Integer> showHotspot;
    private SimpleOption<Integer> hotspotColor;

    public MinecraftCursorSimpleOptions(MinecraftCursorHandler cursorHandler, MinecraftCursorConfigManager configManager, MinecraftCursorHotspot cursorHotspot) {
        this.cursorHandler = cursorHandler;
        this.cursorHotspot = cursorHotspot;

        this.createWidgets();

        this.enabled.setValue(configManager.getEnabled());
        this.scale.setValue(configManager.getScale());
        this.xhot.setValue(configManager.getXhot());
        this.yhot.setValue(configManager.getYhot());
    }

    private void createWidgets() {
        this.enabled = SimpleOption.ofBoolean(
                "minecraft-cursor.options.enabled", MinecraftCursorConfigDefinition.ENABLED.defaultValue, this::enabledCallback);
        this.scale = new SimpleOption<>(
                "minecraft-cursor.options.scale",
                SimpleOption.emptyTooltip(),
                MinecraftCursorSimpleOptions::getDoubleText,
                SimpleOption.DoubleSliderCallbacks.INSTANCE.withModifier(
                        slider -> {
                            double valueInRange = SCALE_MIN_VALUE + ((SCALE_MAX_VALUE - SCALE_MIN_VALUE) * slider);
                            return snapDoubleToNearestStep(valueInRange, SCALE_STEP, SCALE_MIN_VALUE);
                        },
                        value -> {
                            double snappedValue = snapDoubleToNearestStep(value, SCALE_STEP, SCALE_MIN_VALUE);
                            return (snappedValue - SCALE_MIN_VALUE) / (SCALE_MAX_VALUE - SCALE_MIN_VALUE);
                        }),
                MinecraftCursorConfigDefinition.SCALE.defaultValue,
                value -> cursorHandler.updateCursor(value, this.xhot.getValue(), this.yhot.getValue())
        );
        this.xhot = new SimpleOption<>(
                "minecraft-cursor.options.xhot",
                SimpleOption.constantTooltip(Text.translatable("minecraft-cursor.options.xhot.tooltip")),
                MinecraftCursorSimpleOptions::getHotspotSliderText,
                new SimpleOption.ValidatingIntSliderCallbacks(0, cursorHandler.getWidth()),
                MinecraftCursorConfigDefinition.X_HOTSPOT.defaultValue,
                value -> cursorHandler.updateCursor(this.scale.getValue(), value, this.getYhot().getValue())
        );
        this.yhot = new SimpleOption<>(
                "minecraft-cursor.options.yhot",
                SimpleOption.constantTooltip(Text.translatable("minecraft-cursor.options.yhot.tooltip")),
                MinecraftCursorSimpleOptions::getHotspotSliderText,
                new SimpleOption.ValidatingIntSliderCallbacks(0, cursorHandler.getWidth()),
                MinecraftCursorConfigDefinition.Y_HOTSPOT.defaultValue,
                value -> cursorHandler.updateCursor(this.scale.getValue(), this.getXhot().getValue(), value)
        );
        Text hotspotSessionTooltipText = Text.translatable("minecraft-cursor.options.hot.tooltip");
        int showHotspotMax = HotspotVisibility.values.length;
        this.showHotspot = new SimpleOption<>(
                "minecraft-cursor.options.hot",
                SimpleOption.constantTooltip(hotspotSessionTooltipText),
                MinecraftCursorSimpleOptions::getShowHotspotText,
                new SimpleOption.MaxSuppliableIntCallbacks(1, () -> showHotspotMax, showHotspotMax),
                1,
                value -> {
                }
        );
        int hotspotColorsMax = MinecraftCursorHotspot.Color.values.length;
        this.hotspotColor = new SimpleOption<>(
                "minecraft-cursor.options.hot.color",
                SimpleOption.constantTooltip(hotspotSessionTooltipText),
                MinecraftCursorSimpleOptions::getHotspotColorText,
                new SimpleOption.MaxSuppliableIntCallbacks(1, () -> hotspotColorsMax, hotspotColorsMax),
                1,
                value -> this.cursorHotspot.setColor(MinecraftCursorHotspot.Color.getColor(value))
        );
    }

    private void enabledCallback(boolean enabled) {
        this.cursorHandler.enable(enabled);
        if (enabled) {
            cursorHandler.updateCursor(this.scale.getValue(), this.xhot.getValue(), this.yhot.getValue());
        }
    }

    private static Text getDoubleText(Text prefix, double value) {
        return Text.translatable(prefix.getString()).append(String.format(": %.2f", value));
    }

    private static double snapDoubleToNearestStep(double value, @SuppressWarnings("SameParameterValue") double step, @SuppressWarnings("SameParameterValue") double min) {
        return Math.round((value - min) / step) * step + min;
    }

    private static Text getHotspotSliderText(Text prefix, int value) {
        return Text.translatable(prefix.getString()).append(String.format(": %spx", value));
    }

    private static Text getShowHotspotText(Text prefix, int value) {
        return HotspotVisibility.getVisibility(value).getText();
    }

    private static Text getHotspotColorText(Text prefix, int value) {
        return MinecraftCursorHotspot.Color.getColor(value).getText();
    }

    public SimpleOption<Boolean> getEnabled() {
        return this.enabled;
    }

    public SimpleOption<Double> getScale() {
        return this.scale;
    }

    public SimpleOption<Integer> getXhot() {
        return this.xhot;
    }

    public SimpleOption<Integer> getYhot() {
        return this.yhot;
    }

    public SimpleOption<Integer> getShowHotspot() {
        return this.showHotspot;
    }

    public SimpleOption<Integer> getHotspotColor() {
        return hotspotColor;
    }
}
