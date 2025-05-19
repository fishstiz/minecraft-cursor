package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.layouts.LayoutElement;

public interface LayoutElementPatch extends LayoutElement {
    default int getRight() {
        return this.getX() + this.getWidth();
    }

    default int getBottom() {
        return this.getY() + this.getHeight();
    }
}
