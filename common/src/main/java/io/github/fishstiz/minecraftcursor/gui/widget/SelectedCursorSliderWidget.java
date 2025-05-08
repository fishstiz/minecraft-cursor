package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SelectedCursorSliderWidget extends AbstractSliderButton {
    private final Component prefix;
    private final double min;
    private final double max;
    private final double step;
    private final String suffix;
    private final Consumer<Double> onApply;
    private final @Nullable Runnable onRelease;
    private double translatedValue;
    private SelectedCursorButtonWidget inactiveHelperButton;
    private boolean held;
    private TextMapper textMapper;

    public SelectedCursorSliderWidget(
            Component text,
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
            Component text,
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
            Component text,
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

    public void setTextMapper(TextMapper mapper) {
        this.textMapper = mapper;
        this.updateMessage();
    }

    public void update(double translatedValue, boolean active) {
        setTranslatedValue(translatedValue);
        this.active = active;
    }

    public void setTranslatedValue(double translatedValue) {
        value = translatedValueToValue(translatedValue);

        applyValue();
        updateMessage();
    }

    private void translateValue() {
        double scaledValue = min + (value * (max - min));
        translatedValue = Math.round(scaledValue / step) * step;
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);

        SelectedCursorButtonWidget helperButton = getInactiveHelperButton();
        boolean isHelperButtonPresent = helperButton != null;

        if (isHelperButtonPresent) {
            helperButton.active = !active;

            if (isMouseOverInactive(mouseX, mouseY)) {
                PoseStack poseStack = context.pose();
                poseStack.pushPose();
                poseStack.translate(0, 0, 1f);
                helperButton.render(context, mouseX, mouseY, delta);
                poseStack.popPose();
            }
        }

        isHovered = isMouseOver(mouseX, mouseY);

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

        if (isFocused() && previousTranslatedValue != translatedValue) {
            onApply.accept(translatedValue);
        }
    }

    @Override
    protected void updateMessage() {
        MutableComponent label = prefix.copy().append(": ");
        Component message = null;

        if (textMapper != null) {
            Component mappedText = textMapper.getText(translatedValue);
            if (mappedText != null) {
                message = label.append(mappedText);
            }
        }

        if (message == null) {
            String formattedValue = String.format(step % 1 == 0 ? "%.0f" : "%.2f", translatedValue);
            message = label.append(formattedValue + suffix);
        }

        setMessage(message);
    }

    public double getTranslatedValue() {
        return translatedValue;
    }

    public @Nullable SelectedCursorButtonWidget getInactiveHelperButton() {
        if (inactiveHelperButton == null) return null;

        int marginRight = 2;
        int x = (getX() + getWidth()) - inactiveHelperButton.getWidth() - marginRight;
        int y = getY() + (getHeight() - inactiveHelperButton.getHeight()) / 2;

        inactiveHelperButton.setPosition(x, y);
        return inactiveHelperButton;
    }

    public void setInactiveHelperButton(SelectedCursorButtonWidget helperButton, int width, int height) {
        this.inactiveHelperButton = helperButton;
        this.inactiveHelperButton.setWidth(width);
        this.inactiveHelperButton.height = height;
    }

    public Component getPrefix() {
        return this.prefix;
    }

    public boolean isMouseOverInactive(int mouseX, int mouseY) {
        return !active
                && mouseX >= getX()
                && mouseY >= getY()
                && mouseX < getX() + getWidth()
                && mouseY < getY() + getHeight();
    }

    @FunctionalInterface
    public interface TextMapper {
        @Nullable Component getText(double translatedValue);
    }
}