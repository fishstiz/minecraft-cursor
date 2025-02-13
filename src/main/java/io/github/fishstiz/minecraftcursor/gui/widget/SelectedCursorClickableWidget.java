package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public abstract class SelectedCursorClickableWidget extends ClickableWidget {
    public SelectedCursorClickableWidget(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    public int getRight() {
        return this.getX() + this.getWidth();
    }

    public int getBottom() {
        return this.getY() + this.getHeight();
    }
}
