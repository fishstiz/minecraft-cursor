package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class SelectedCursorToggleWidget extends Button {
    protected boolean value;
    protected Component prefix;
    protected Consumer<Boolean> handlePress;

    public SelectedCursorToggleWidget(Component prefix, boolean defaultValue, Consumer<Boolean> onPress) {
        this(0, 0, 150, 20, prefix, defaultValue, onPress);
    }

    protected SelectedCursorToggleWidget(
            int x,
            int y,
            int width,
            int height,
            Component prefix,
            boolean defaultValue,
            Consumer<Boolean> onPress
    ) {
        super(x, y, width, height, prefix, SelectedCursorToggleWidget::handlePressButton, Button.DEFAULT_NARRATION);

        this.prefix = prefix;
        this.value = defaultValue;
        this.handlePress = onPress;
        updateMessage();
    }

    protected static void handlePressButton(Button buttonWidget) {
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
        setMessage(prefix.copy().append(": ").append(value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF));
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        setFocused(false);
    }
}
