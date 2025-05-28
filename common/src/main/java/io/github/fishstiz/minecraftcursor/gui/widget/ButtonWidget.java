package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ButtonWidget extends Button {
    private static final int DEFAULT_SPRITE_SIZE = 16;
    private @Nullable ResourceLocation sprite;
    private @Nullable ResourceLocation inactiveSprite;
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

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSize(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    public ButtonWidget spriteOnly(@NotNull ResourceLocation sprite, ResourceLocation inactiveSprite, int textureWidth, int textureHeight) {
        this.sprite = sprite;
        this.inactiveSprite = inactiveSprite;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        return this;
    }

    public ButtonWidget spriteOnly(@NotNull ResourceLocation sprite) {
        return this.spriteOnly(sprite, sprite, DEFAULT_SPRITE_SIZE, DEFAULT_SPRITE_SIZE);
    }

    public ButtonWidget spriteOnly(@NotNull ResourceLocation sprite, ResourceLocation inactiveSprite) {
        return this.spriteOnly(sprite, inactiveSprite, DEFAULT_SPRITE_SIZE, DEFAULT_SPRITE_SIZE);
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
            ResourceLocation currentSprite = this.active ? this.sprite : Objects.requireNonNullElse(this.inactiveSprite, this.sprite);
            int spriteWidth = Math.min(this.getWidth(), this.textureWidth);
            int spriteHeight = Math.min(this.getHeight(), this.textureHeight);
            int spriteX = this.getX() + (this.getWidth() - spriteWidth) / 2;
            int spriteY = this.getY() + (this.getHeight() - spriteHeight) / 2;

            guiGraphics.blit(
                    currentSprite,
                    spriteX, spriteY,
                    spriteWidth, spriteHeight,
                    0,0,
                    this.textureWidth, this.textureHeight,
                    this.textureWidth, this.textureHeight
            );
        }
    }
}
