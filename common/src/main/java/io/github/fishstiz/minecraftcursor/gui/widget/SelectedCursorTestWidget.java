package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.util.DrawUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.MOD_ID;

public class SelectedCursorTestWidget extends AbstractWidget implements LayoutElementPatch, CursorProvider {
    private static final ResourceLocation BACKGROUND_64 = new ResourceLocation(MOD_ID, "textures/gui/background_dark_64.png");
    private static final int BACKGROUND_DISABLED = 0x7F000000; // 50% black
    private static final int BORDER_COLOR = 0xFF000000; // black
    private static final int HOTSPOT_RULER_COLOR = 0xFF00FF00; // green
    private final Button testButton = Button.builder(Component.empty(), b -> b.setFocused(false)).size(20, 20).build();
    private final CursorOptionsWidget options;

    public SelectedCursorTestWidget(int size, CursorOptionsWidget optionsWidget) {
        super(optionsWidget.getX(), optionsWidget.getY(), size, size, Component.empty());
        this.options = optionsWidget;

        this.active = false;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        float cellSize = (float) getWidth() / this.options.parent().getSelectedCursor().getTextureWidth();
        DrawUtil.drawCheckerboard(context, getX(), getY(), getWidth(), getHeight(), cellSize, BACKGROUND_64, 64);

        if (this.isEnabled()) {
            int x = getX() + (getWidth() / 2 - testButton.getWidth() / 2);
            int y = getY() + (getHeight() / 2 - testButton.getHeight() / 2);
            testButton.setPosition(x, y);
            testButton.render(context, mouseX, mouseY, delta);
            renderRuler(context, mouseX, mouseY);
        } else {
            context.fill(getX(), getY(), getRight(), getBottom(), BACKGROUND_DISABLED);
        }

        context.renderOutline(getX(), getY(), getWidth(), getHeight(), BORDER_COLOR);
    }

    private void renderRuler(GuiGraphics context, int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            context.hLine(getX(), getRight() - 1, mouseY, HOTSPOT_RULER_COLOR);
            context.vLine(mouseX, getY(), getBottom(), HOTSPOT_RULER_COLOR);
        }
    }

    public boolean isEnabled() {
        return options.parent().getSelectedCursor().isEnabled();
    }

    @Override
    public CursorType getCursorType(double mouseX, double mouseY) {
        return options.parent().getSelectedCursor().getType();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.getX()
               && mouseY >= this.getY()
               && mouseX < this.getRight()
               && mouseY < this.getBottom();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isEnabled() && this.isValidClickButton(button)) {
            testButton.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput builder) {
        this.defaultButtonNarrationText(builder);
    }
}
