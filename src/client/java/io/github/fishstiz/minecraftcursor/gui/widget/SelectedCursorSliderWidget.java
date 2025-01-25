package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SelectedCursorSliderWidget extends SliderWidget {
    private final Text prefix;
    private final double min;
    private final double max;
    private final double step;
    private final String suffix;
    private final Consumer<Double> onApply;
    private final @Nullable Runnable onRelease;
    private double translatedValue;

    SelectedCursorSliderWidget(
            Text text,
            double defaultValue,
            double min,
            double max,
            double step,
            Consumer<Double> onApply,
            Runnable onRelease,
            SelectedCursorOptionsWidget optionsWidget
    ) {
        this(text, defaultValue, min, max, step, "", onApply, onRelease, optionsWidget);
    }

    SelectedCursorSliderWidget(
            Text text,
            double defaultValue,
            double min,
            double max,
            double step,
            String suffix,
            Consumer<Double> onApply,
            SelectedCursorOptionsWidget optionsWidget
    ) {
        this(text, defaultValue, min, max, step, suffix, onApply, null, optionsWidget);
    }

    SelectedCursorSliderWidget(
            Text text,
            double defaultValue,
            double min,
            double max,
            double step,
            String suffix,
            Consumer<Double> onApply,
            @Nullable Runnable onRelease,
            SelectedCursorOptionsWidget optionsWidget
    ) {
        super(optionsWidget.getX(), optionsWidget.getY(), optionsWidget.getWidth() / 2, SelectedCursorOptionsWidget.OPTIONS_HEIGHT, text, defaultValue);

        this.prefix = text;
        this.min = min;
        this.max = max;
        this.step = step;
        this.suffix = suffix;
        this.onApply = onApply;
        this.onRelease = onRelease;

        translateValue();
        updateMessage();
    }

    public void setValue(double translatedValue) {
        double clampedValue = Math.max(min, Math.min(translatedValue, max));
        value = (clampedValue - min) / (max - min);

        applyValue();
        updateMessage();
    }

    private void translateValue() {
        double scaledValue = min + (value * (max - min));
        translatedValue = Math.round(scaledValue / step) * step;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        hovered = isMouseOver(mouseX, mouseY);
    }

    @Override
    protected void applyValue() {
        double previousTranslatedValue = translatedValue;

        translateValue();

        if (previousTranslatedValue != translatedValue) {
            onApply.accept(translatedValue);
        }
    }

    @Override
    protected void updateMessage() {
        String formattedValue = String.format(step % 1 == 0 ? "%.0f" : "%.2f", translatedValue);
        setMessage(Text.empty().append(prefix).append(Text.of(": " + formattedValue + suffix)));
    }

    public double getTranslatedValue() {
        return translatedValue;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        setFocused(false);

        if (this.onRelease != null) {
            this.onRelease.run();
        }
    }
}