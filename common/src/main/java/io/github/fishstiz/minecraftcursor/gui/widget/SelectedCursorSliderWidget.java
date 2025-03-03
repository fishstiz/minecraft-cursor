package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
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
        SelectedCursorButtonWidget helperButton = getInactiveHelperButton();
        boolean isHelperButtonPresent = helperButton != null;

        if (isHelperButtonPresent) {
            helperButton.active = !active;
        }

        if (isHelperButtonPresent && isMouseOverInactive(mouseX, mouseY)) {
            renderAroundHelperButton(context, mouseX, mouseY, delta, helperButton);
            helperButton.render(context, mouseX, mouseY, delta);
        } else {
            super.renderWidget(context, mouseX, mouseY, delta);
        }

        isHovered = isMouseOver(mouseX, mouseY);
    }

    private void renderAroundHelperButton(
            GuiGraphics context,
            int mouseX,
            int mouseY,
            float delta,
            SelectedCursorButtonWidget helperButton
    ) {
        int x = getX();
        int y = getY();
        int right = getRight();
        int bottom = getBottom();
        int helperY = helperButton.getY();
        int helperBottom = helperButton.getBottom();

        renderSection(context, mouseX, mouseY, delta, x, y, right, helperY); // top
        renderSection(context, mouseX, mouseY, delta, x, helperBottom, right, bottom); // bottom
        renderSection(context, mouseX, mouseY, delta, x, helperY, helperButton.getX(), helperBottom); // left
        renderSection(context, mouseX, mouseY, delta, helperButton.getRight(), helperY, right, helperBottom); // right
    }

    private void renderSection(GuiGraphics context, int mouseX, int mouseY, float delta, int x1, int y1, int x2, int y2) {
        context.enableScissor(x1, y1, x2, y2);
        super.renderWidget(context, mouseX, mouseY, delta);
        context.disableScissor();
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
        String formattedValue = String.format(step % 1 == 0 ? "%.0f" : "%.2f", translatedValue);
        setMessage(Component.empty().append(prefix).append(Component.nullToEmpty(": " + formattedValue + suffix)));
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
        this.inactiveHelperButton.setSize(width, height);
    }

    public Component getPrefix() {
        return this.prefix;
    }

    public boolean isMouseOverInactive(int mouseX, int mouseY) {
        return !active
                && mouseX >= getX()
                && mouseY >= getY()
                && mouseX < getRight()
                && mouseY < getBottom();
    }
}