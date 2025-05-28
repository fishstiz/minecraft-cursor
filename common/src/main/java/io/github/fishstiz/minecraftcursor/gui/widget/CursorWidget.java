package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.util.DrawUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class CursorWidget extends AbstractWidget implements CursorProvider {
    public static final int DEFAULT_HEIGHT = 32;
    public static final int DEFAULT_WIDTH = 32;
    private static final int BORDER_COLOR = 0xFF000000; // black
    private static final int FOCUSED_BORDER_COLOR = 0xFFFFFFFF; // white
    private final ResourceLocation background64;
    private final Cursor cursor;

    protected CursorWidget(
            int x,
            int y,
            int width,
            int height,
            @NotNull Component message,
            @NotNull Cursor cursor,
            @NotNull ResourceLocation background64
    ) {
        super(x, y, width, height, message);

        this.cursor = cursor;
        this.background64 = background64;
    }

    protected CursorWidget(@NotNull Component message, @NotNull Cursor cursor, @NotNull ResourceLocation background64) {
        this(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, message, cursor, background64);
    }

    protected void renderCursor(@NotNull GuiGraphics guiGraphics, @NotNull Cursor cursor) {
    }

    protected abstract void renderRuler(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY);

    protected void renderBackground(@NotNull GuiGraphics guiGraphics) {
        DrawUtil.drawCheckerboard(
                guiGraphics,
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                this.getCellSize(),
                this.background64,
                64
        );
    }

    protected void renderBorder(@NotNull GuiGraphics guiGraphics) {
        int color = this.isFocused() && this.active ? FOCUSED_BORDER_COLOR : BORDER_COLOR;
        guiGraphics.renderOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), color);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.cursor.isLoaded()) {
            this.renderBackground(guiGraphics);
            this.renderCursor(guiGraphics, this.cursor);
            this.renderRuler(guiGraphics, mouseX, mouseY);
        }
        this.renderBorder(guiGraphics);
    }

    protected @NotNull Cursor getCursor() {
        return this.cursor;
    }

    protected float getCellSize() {
        Cursor currentCursor = this.getCursor();
        return currentCursor.isLoaded()
                ? (float) getWidth() / this.getCursor().getTextureWidth()
                : 0;
    }

    @Override
    public int getRight() {
        return this.getX() + this.getWidth(); // for pre 1.21
    }

    @Override
    public int getBottom() {
        return this.getY() + this.getHeight(); // for pre 1.21
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
    }
}
