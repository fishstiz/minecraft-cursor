package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElementSlidingBackground {
    private static final float ANIMATION_SPEED = 25;
    private final boolean outlineOnly;
    private final int color;
    private float lastY = 0;
    private long lastFrameTime = Util.getNanos();

    public ElementSlidingBackground(int color, boolean outlineOnly) {
        this.color = color;
        this.outlineOnly = outlineOnly;
    }

    public ElementSlidingBackground(int color) {
        this(color, false);
    }

    public void reset() {
        this.lastY = 0;
        this.lastFrameTime = Util.getNanos();
    }

    public void render(@NotNull GuiGraphics guiGraphics, int x, int y, int width, int height, float partialTick) {
        long now = Util.getNanos();
        float deltaTime = (now - this.lastFrameTime) / 1_000_000_000f;
        this.lastFrameTime = now;

        this.lastY = this.lastY == 0 ? y : this.lastY;

        if (Math.abs(this.lastY - y) < 0.5f) {
            this.lastY = y;
        } else {
            float alpha = 1f - (float) Math.exp(-ANIMATION_SPEED * deltaTime);
            this.lastY = Mth.lerp(alpha, this.lastY, y);
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