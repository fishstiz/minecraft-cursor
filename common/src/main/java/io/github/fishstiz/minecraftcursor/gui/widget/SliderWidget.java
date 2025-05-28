package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.gui.MouseEvent;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static net.minecraft.client.gui.components.Button.DEFAULT_HEIGHT;
import static net.minecraft.client.gui.components.Button.DEFAULT_WIDTH;

public class SliderWidget extends AbstractSliderButton {
    private final Consumer<Double> listener;
    private final Component prefix;
    private final @Nullable Component suffix;
    private final @Nullable TextMapper textMapper;
    private final @Nullable MouseEventListener mouseEventListener;
    private final double min;
    private final double max;
    private final double step;
    private double mappedValue;
    private double previousAppliedValue;

    public SliderWidget(
            int x,
            int y,
            int width,
            int height,
            double mappedValue,
            double min,
            double max,
            double step,
            @NotNull Consumer<Double> listener,
            @NotNull Component prefix,
            @Nullable Component suffix,
            @Nullable TextMapper textMapper,
            @Nullable MouseEventListener mouseEventListener
    ) {
        super(x, y, width, height, prefix, mappedValue);

        this.listener = listener;
        this.prefix = prefix;
        this.suffix = suffix;
        this.textMapper = textMapper;
        this.min = min;
        this.max = max;
        this.step = step;
        this.mappedValue = mappedValue;
        this.mouseEventListener = mouseEventListener;

        this.value = this.unmapValue(this.mappedValue);

        this.mapValue();
        this.updateMessage();

        this.previousAppliedValue = this.mappedValue;
    }

    public SliderWidget(
            double mappedValue,
            double min,
            double max,
            double step,
            @NotNull Consumer<Double> listener,
            @NotNull Component prefix,
            @Nullable Component suffix,
            @Nullable TextMapper textMapper,
            @Nullable MouseEventListener mouseEventListener
    ) {
        this(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, mappedValue, min, max, step, listener, prefix, suffix, textMapper, mouseEventListener);
    }

    public SliderWidget(
            double mappedValue,
            double min,
            double max,
            double step,
            @NotNull Consumer<Double> listener,
            @NotNull Component prefix,
            @Nullable Component suffix
    ) {
        this(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, mappedValue, min, max, step, listener, prefix, suffix, null, null);
    }

    @Override
    protected void updateMessage() {
        MutableComponent label = this.prefix.copy().append(": ");
        MutableComponent message = null;

        if (this.textMapper != null) {
            Component mappedText = textMapper.getText(this.mappedValue);
            if (mappedText != null) {
                message = label.append(mappedText);
            }
        }

        if (message == null) {
            String formatted = String.format(step % 1 == 0 ? "%.0f" : "%.2f", this.mappedValue);
            message = label.append(formatted);
            if (this.suffix != null) {
                message.append(this.suffix);
            }
        }

        this.setMessage(message);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        this.sendMouseEvent(MouseEvent.CLICK);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        super.onDrag(mouseX, mouseY, dragX, dragY);
        this.sendMouseEvent(MouseEvent.DRAG);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        this.setFocused(false);
        this.sendMouseEvent(MouseEvent.RELEASE);
    }

    private void sendMouseEvent(MouseEvent mouseEvent) {
        if (this.mouseEventListener != null) {
            this.mouseEventListener.onMouseEvent(this, mouseEvent, this.mappedValue);
        }
    }

    @Override
    protected void applyValue() {
        this.mapValue();

        if (this.previousAppliedValue != this.mappedValue) {
            this.previousAppliedValue = this.mappedValue;
            this.listener.accept(this.mappedValue);
        }
    }

    private double unmapValue(double mappedValue) {
        double clampedValue = Math.max(min, Math.min(mappedValue, max));
        return (clampedValue - min) / (max - min);
    }

    private void mapValue() {
        double scaledValue = this.min + (this.value * (this.max - this.min));
        this.mappedValue = Math.round(scaledValue / this.step) * this.step;
    }

    public double getMappedValue() {
        return this.mappedValue;
    }

    public void applyMappedValue(double translatedValue) {
        this.value = this.unmapValue(translatedValue);
        this.applyValue();
        this.updateMessage();
    }

    public Component getPrefix() {
        return this.prefix;
    }

    @FunctionalInterface
    public interface MouseEventListener {
        void onMouseEvent(@NotNull SliderWidget target, @NotNull MouseEvent mouseEvent, double mappedValue);
    }

    @FunctionalInterface
    public interface TextMapper {
        @Nullable Component getText(double mappedValue);
    }
}
