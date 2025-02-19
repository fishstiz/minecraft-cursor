package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class SelectedCursorToggleWidget extends ButtonWidget {
    protected boolean value;
    protected Text prefix;
    protected Consumer<Boolean> handlePress;

    public SelectedCursorToggleWidget(Text prefix, boolean defaultValue, Consumer<Boolean> onPress) {
        this(0, 0, 150, 20, prefix, defaultValue, onPress);
    }

    protected SelectedCursorToggleWidget(
            int x,
            int y,
            int width,
            int height,
            Text prefix,
            boolean defaultValue,
            Consumer<Boolean> onPress
    ) {
        super(x, y, width, height, prefix, SelectedCursorToggleWidget::handlePressButton, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);

        this.prefix = prefix;
        this.value = defaultValue;
        this.handlePress = onPress;
        updateMessage();
    }

    protected static void handlePressButton(ButtonWidget buttonWidget) {
        SelectedCursorToggleWidget toggleWidget = (SelectedCursorToggleWidget) buttonWidget;
        toggleWidget.toggleValue();
        toggleWidget.handlePress.accept(toggleWidget.value);
    }

    protected void toggleValue() {
        value = !value;
        updateMessage();
    }

    public void setValue(boolean value) {
        this.value = value;
        updateMessage();
    }

    protected void updateMessage() {
        setMessage(prefix.copy().append(": ").append(value ? ScreenTexts.ON : ScreenTexts.OFF));
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        setFocused(false);
    }
}
