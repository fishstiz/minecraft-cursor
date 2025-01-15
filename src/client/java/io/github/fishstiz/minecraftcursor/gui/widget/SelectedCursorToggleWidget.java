package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class SelectedCursorToggleWidget extends ButtonWidget {
    private boolean value;
    private Text prefix;
    private Consumer<Boolean> onPressConsumer;

    protected SelectedCursorToggleWidget(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
        super(x, y, width, height, message, onPress, narrationSupplier);
    }

    public static SelectedCursorToggleWidget build(Text prefix, boolean defaultValue, Consumer<Boolean> onPress) {
        return new SelectedCursorToggleWidget(0, 0, 150, 20,
                prefix, SelectedCursorToggleWidget::onPressButton, ButtonWidget.DEFAULT_NARRATION_SUPPLIER)
                .initialize(prefix, defaultValue, onPress);
    }

    private SelectedCursorToggleWidget initialize(Text prefix, boolean defaultValue, Consumer<Boolean> onPress) {
        this.prefix = prefix;
        this.value = defaultValue;
        this.onPressConsumer = onPress;
        updateMessage();

        return this;
    }

    private static void onPressButton(ButtonWidget buttonWidget) {
        SelectedCursorToggleWidget toggleWidget = (SelectedCursorToggleWidget) buttonWidget;
        toggleWidget.toggleValue();
        toggleWidget.onPressConsumer.accept(toggleWidget.value);
    }

    private void toggleValue() {
        value = !value;
        updateMessage();
    }

    public void setValue(boolean value) {
        this.value = value;
        updateMessage();
    }

    private void updateMessage() {
        Text message = Text.translatable(prefix.getString())
                .append(": ")
                .append(value ? ScreenTexts.ON : ScreenTexts.OFF);

        setMessage(message);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        setFocused(false);
    }
}
