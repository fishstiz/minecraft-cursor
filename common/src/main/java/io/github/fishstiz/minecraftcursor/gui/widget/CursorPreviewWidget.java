package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.util.DrawUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CursorPreviewWidget extends CursorWidget {
    private static final ResourceLocation BACKGROUND_64 = MinecraftCursor.loc("textures/gui/background_dark_64.png");
    private static final Component PREVIEW_TEXT = Component.translatable("minecraft-cursor.options.preview");
    private static final int PREVIEW_TEXT_OFFSET = 4;
    private static final int PREVIEW_TEXT_COLOR = 0x7FFFFFFF; // 50% white
    private static final int RULER_COLOR = 0xFF00FF00; // green
    private static final int BACKGROUND_DISABLED = 0x7F000000; // 50% black
    private static final int DEFAULT_BUTTON_SIZE = 20;
    private final @Nullable Button button;
    private final Font font;

    public CursorPreviewWidget(@NotNull Cursor cursor, @NotNull Font font, @Nullable Button button) {
        super(CommonComponents.EMPTY, cursor, BACKGROUND_64);

        this.active = false;
        this.font = font;
        this.button = button;
    }

    public CursorPreviewWidget(@NotNull Cursor cursor, @NotNull Font font) {
        this(cursor, font, Button.builder(CommonComponents.EMPTY, b -> b.setFocused(false))
                .size(DEFAULT_BUTTON_SIZE, DEFAULT_BUTTON_SIZE)
                .build()
        );
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Cursor cursor = this.getCursor();

        if (cursor.isLoaded()) {
            this.renderBackground(guiGraphics);
            if (cursor.isEnabled()) {
                this.renderPreviewText(guiGraphics);
                this.renderButton(guiGraphics, mouseX, mouseY, partialTick);
                this.renderRuler(guiGraphics, mouseX, mouseY);
            }
        }
        if (!cursor.isEnabled()) {
            guiGraphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), BACKGROUND_DISABLED);
        }

        this.renderBorder(guiGraphics);
    }

    protected void renderPreviewText(@NotNull GuiGraphics guiGraphics) {
        int width = this.font.width(PREVIEW_TEXT);
        int endX = this.getRight() - PREVIEW_TEXT_OFFSET;
        int endY = this.getBottom() - PREVIEW_TEXT_OFFSET;
        int startX = endX - width;
        int startY = endY - this.font.lineHeight;
        DrawUtil.drawScrollableTextLeftAlign(guiGraphics, this.font, PREVIEW_TEXT, startX, startY, endX, endY, PREVIEW_TEXT_COLOR, false);
        guiGraphics.flush();
    }

    protected void renderButton(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.button != null) {
            int buttonX = this.getX() + (this.getWidth() / 2 - this.button.getWidth() / 2);
            int buttonY = this.getY() + (this.getHeight() / 2 - this.button.getHeight() / 2);
            this.button.setPosition(buttonX, buttonY);
            this.button.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void renderRuler(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.isMouseOver(mouseX, mouseY)) {
            guiGraphics.hLine(this.getX(), this.getRight() - 1, mouseY, RULER_COLOR);
            guiGraphics.vLine(mouseX, getY(), this.getBottom(), RULER_COLOR);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.button != null && this.button.isMouseOver(mouseX, mouseY)) {
            this.button.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible
               && mouseX >= (double) this.getX()
               && mouseY >= (double) this.getY()
               && mouseX < (double) this.getRight()
               && mouseY < (double) this.getBottom();
    }

    @Override
    public CursorType getCursorType(double mouseX, double mouseY) {
        return this.getCursor().getType();
    }
}
