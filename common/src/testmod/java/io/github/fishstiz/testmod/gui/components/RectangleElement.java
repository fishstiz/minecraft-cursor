package io.github.fishstiz.testmod.gui.components;

import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class RectangleElement extends AbstractWidget implements CursorProvider {
    private int color = 0xFF000000;
    private CursorType cursorType;

    public RectangleElement(Component message) {
        super(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, message);
    }

    public RectangleElement color(int color) {
        this.color = color;
        return this;
    }

    public RectangleElement cursorType(CursorType cursorType) {
        this.cursorType = cursorType;
        return this;
    }

    public RectangleElement pos(int x, int y) {
        this.setX(x);
        this.setY(y);
        return this;
    }

    public RectangleElement apply(Consumer<RectangleElement> consumer) {
        consumer.accept(this);
        return this;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int left = this.getX();
        int top = this.getY();
        int right = left + this.getWidth();
        int bottom = top + this.getHeight();
        final var font = Minecraft.getInstance().font;

        guiGraphics.fill(left, top, right, bottom, this.color);
        guiGraphics.drawString(font, this.getMessage(), left + 1, bottom - font.lineHeight - 1, 0xFF000000);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.getMessage());
    }

    @Override
    public CursorType getCursorType(double mouseX, double mouseY) {
        return this.cursorType;
    }
}
