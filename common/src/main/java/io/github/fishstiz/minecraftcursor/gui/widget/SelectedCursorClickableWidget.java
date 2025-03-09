package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public abstract class SelectedCursorClickableWidget extends AbstractWidget {
    public SelectedCursorClickableWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    public int getRight() {
        return this.getX() + this.getWidth();
    }

    public int getBottom() {
        return this.getY() + this.getHeight();
    }
}
