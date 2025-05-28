package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.NotNull;

public interface ElementView extends GuiEventListener {
    int getX();

    int getY();

    int getWidth();

    int getHeight();

    default int getRight() {
        return this.getX() + this.getWidth();
    }

    default int getBottom() {
        return this.getY() + this.getHeight();
    }

    @Override
    default @NotNull ScreenRectangle getRectangle() {
        return new ScreenRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }
}
