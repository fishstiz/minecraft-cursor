package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class SelectedCursorButtonWidget extends ButtonWidget {
    public SelectedCursorButtonWidget(Text message, Runnable onPress) {
        super(0, 0, 150, 20, message, b -> onPress.run(), ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        setFocused(false);
    }
}
