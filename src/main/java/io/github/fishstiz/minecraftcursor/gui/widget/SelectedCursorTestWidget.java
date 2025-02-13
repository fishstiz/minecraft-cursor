package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SelectedCursorTestWidget extends ClickableWidget implements CursorProvider {
    private static final Identifier BACKGROUND = Identifier.of(MinecraftCursor.MOD_ID, "textures/gui/test_background.png");
    private static final int HOTSPOT_RULER_COLOR = 0xFF00FF00; // green
    private final SelectedCursorOptionsWidget optionsWidget;
    ButtonWidget hoverButton = ButtonWidget.builder(Text.empty(),
            b -> b.setFocused(false)).size(20, 20).build();

    public SelectedCursorTestWidget(int size, SelectedCursorOptionsWidget optionsWidget) {
        super(optionsWidget.getX(), optionsWidget.getY(), size, size, Text.empty());
        this.optionsWidget = optionsWidget;

        this.active = false;
        placeButton();
    }

    private void placeButton() {
        int x = getX() + (getWidth() / 2 - hoverButton.getWidth() / 2);
        int y = getY() + (getHeight() / 2 - hoverButton.getHeight() / 2);
        hoverButton.setPosition(x, y);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        Cursor cursor = optionsWidget.optionsScreen.getSelectedCursor();
        context.drawTexture(BACKGROUND, getX(), getY(), 0, 0, width, height, width, height);

        if (cursor.isEnabled()) {
            hoverButton.render(context, mouseX, mouseY, delta);
            renderRuler(context, mouseX, mouseY);
        } else {
            context.fill(getX(), getY(), getRight(), getBottom(), 0x7F000000); // 50% black overlay
        }

        context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0xFF000000);
    }

    private void renderRuler(DrawContext context, int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            context.drawHorizontalLine(getX(), getRight() - 1, mouseY, HOTSPOT_RULER_COLOR);
            context.drawVerticalLine(mouseX, getY(), getBottom(), HOTSPOT_RULER_COLOR);
        }
    }

    @Override
    public CursorType getCursorType(double mouseX, double mouseY) {
        return optionsWidget.optionsScreen.getSelectedCursor().getType();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.getX()
                && mouseY >= this.getY()
                && mouseX < this.getRight()
                && mouseY < this.getBottom();
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        placeButton();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
