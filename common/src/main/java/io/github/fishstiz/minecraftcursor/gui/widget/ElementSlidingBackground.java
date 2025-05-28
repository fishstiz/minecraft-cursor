package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElementSlidingBackground {
    private final boolean outlineOnly;
    private final int color;
    private float lastY = 0;

    public ElementSlidingBackground(int color, boolean outlineOnly) {
        this.color = color;
        this.outlineOnly = outlineOnly;
    }

    public ElementSlidingBackground(int color) {
        this(color, false);
    }

    public void reset() {
        this.lastY = 0;
    }

    public void render(@NotNull GuiGraphics guiGraphics, int x, int y, int width, int height, float partialTick) {
        this.lastY = this.lastY == 0 ? y : this.lastY;

        if (Math.abs(this.lastY - y) < 0.5f) {
            this.lastY = y;
        } else {
            this.lastY = Mth.lerp(partialTick, this.lastY, y);
        }

        int right = x + width;
        int bottom = (int) this.lastY + height;

        if (this.outlineOnly) {
            guiGraphics.renderOutline(x, (int) this.lastY, width, height, this.color);
        } else {
            guiGraphics.fill(x, (int) this.lastY, right, bottom, this.color);
        }
    }

    public void render(@NotNull GuiGraphics guiGraphics, @Nullable ElementView element, float partialTick) {
        if (element == null) {
            this.reset();
            return;
        }

        this.render(guiGraphics, element.getX(), element.getY(), element.getWidth(), element.getHeight(), partialTick);
    }
}