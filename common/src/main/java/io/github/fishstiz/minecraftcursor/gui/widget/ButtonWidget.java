package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ButtonWidget extends Button {
    private static final int DEFAULT_SPRITE_SIZE = 16;
    private static final int DISABLED_SPRITE_COLOR = 0x80A0A0A0; // 50% gray
    private @Nullable ResourceLocation sprite;
    private int textureWidth;
    private int textureHeight;

    public ButtonWidget(int x, int y, int width, int height, Component message, Runnable onPress) {
        super(x, y, width, height, message, btn -> onPress.run(), Button.DEFAULT_NARRATION);
    }

    public ButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, Button.DEFAULT_NARRATION);
    }

    public ButtonWidget(Component message, Runnable onPress) {
        this(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, message, onPress);
    }

    public ButtonWidget(Component message, OnPress onPress) {
        this(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, message, onPress);
    }

    public ButtonWidget withSize(int size) {
        this.setSize(size, size);
        return this;
    }

    public ButtonWidget withTooltip(@Nullable Tooltip tooltip) {
        this.setTooltip(tooltip);
        return this;
    }

    public ButtonWidget withTooltip(@NotNull Component message) {
        this.setTooltip(Tooltip.create(message));
        return this;
    }

    public ButtonWidget spriteOnly(@NotNull ResourceLocation sprite, int textureWidth, int textureHeight) {
        this.sprite = sprite;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        return this;
    }

    public ButtonWidget spriteOnly(@NotNull ResourceLocation sprite) {
        return this.spriteOnly(sprite, DEFAULT_SPRITE_SIZE, DEFAULT_SPRITE_SIZE);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.setFocused(false);
    }

    @Override
    protected void renderScrollingString(@NotNull GuiGraphics guiGraphics, @NotNull Font font, int width, int color) {
        if (this.sprite == null) {
            super.renderScrollingString(guiGraphics, font, width, color);
        }
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        if (this.sprite != null) {
            int spriteWidth = Math.min(this.getWidth(), this.textureWidth);
            int spriteHeight = Math.min(this.getHeight(), this.textureHeight);
            int spriteX = this.getX() + (this.getWidth() - spriteWidth) / 2;
            int spriteY = this.getY() + (this.getHeight() - spriteHeight) / 2;
            int color = this.active ? -1 : DISABLED_SPRITE_COLOR;

            guiGraphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    this.sprite,
                    spriteX, spriteY,
                    0,0,
                    this.textureWidth, this.textureHeight,
                    spriteWidth, spriteHeight,
                    this.textureWidth, this.textureHeight,
                    color
            );
        }
    }
}
