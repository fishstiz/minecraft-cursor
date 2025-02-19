package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
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
    private boolean held;

    public SelectedCursorSliderWidget(
            Text text,
            double defaultValue,
            double min,
            double max,
            double step,
            Consumer<Double> onApply,
            Runnable onRelease
    ) {
        this(text, defaultValue, min, max, step, "", onApply, onRelease);
    }

    public SelectedCursorSliderWidget(
            Text text,
            double defaultValue,
            double min,
            double max,
            double step,
            String suffix,
            Consumer<Double> onApply
    ) {
        this(text, defaultValue, min, max, step, suffix, onApply, null);
    }

    public SelectedCursorSliderWidget(
            Text text,
            double defaultValue,
            double min,
            double max,
            double step,
            String suffix,
            Consumer<Double> onApply,
            @Nullable Runnable onRelease
    ) {
        super(0, 0, 0, 0, text, defaultValue);

        this.prefix = text;
        this.min = min;
        this.max = max;
        this.step = step;
        this.suffix = suffix;
        this.onApply = onApply;
        this.onRelease = onRelease;

        this.value = translatedValueToValue(defaultValue);

        translateValue();
        updateMessage();
    }

    private double translatedValueToValue(double translatedValue) {
        double clampedValue = Math.max(min, Math.min(translatedValue, max));
        return (clampedValue - min) / (max - min);
    }

    public void setValue(double translatedValue) {
        value = translatedValueToValue(translatedValue);

        applyValue();
        updateMessage();
    }

    private void translateValue() {
        double scaledValue = min + (value * (max - min));
        translatedValue = Math.round(scaledValue / step) * step;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        hovered = isMouseOver(mouseX, mouseY);
        if (held && !CursorTypeUtil.isLeftClickHeld()) {
            if (this.onRelease != null) {
                this.onRelease.run();
            }
            setFocused(false);
            held = false;
        } else {
            held = CursorTypeUtil.isLeftClickHeld();
        }
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
        System.out.println("IS IT FUCKING RELEASED OR WHAT");
        super.onRelease(mouseX, mouseY);
    }


    //    @Override
//    public void onRelease(double mouseX, double mouseY) {
//        System.out.println("RELEASING");
//        super.onRelease(mouseX, mouseY);
//        setFocused(false);
//
//        if (this.onRelease != null) {
//            this.onRelease.run();
//        }
//    }
}