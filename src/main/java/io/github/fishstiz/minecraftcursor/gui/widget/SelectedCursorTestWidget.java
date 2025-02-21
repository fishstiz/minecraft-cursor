package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SelectedCursorTestWidget extends SelectedCursorClickableWidget implements CursorProvider {
    private static final Identifier BACKGROUND = Identifier.of(MinecraftCursor.MOD_ID, "textures/gui/test_background.png");
    private static final int HOTSPOT_RULER_COLOR = 0xFF00FF00; // green
    private static final ButtonWidget BUTTON = ButtonWidget.builder(Text.empty(),
            b -> b.setFocused(false)).size(20, 20).build();
    private final CursorOptionsWidget options;

    public SelectedCursorTestWidget(int size, CursorOptionsWidget optionsWidget) {
        super(optionsWidget.getX(), optionsWidget.getY(), size, size, Text.empty());
        this.options = optionsWidget;

        this.active = false;
    }

    private void placeButton() {
        int x = getX() + (getWidth() / 2 - BUTTON.getWidth() / 2);
        int y = getY() + (getHeight() / 2 - BUTTON.getHeight() / 2);
        BUTTON.setPosition(x, y);
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(BACKGROUND, getX(), getY(), 0, 0, width, height, width, height);

        if (options.parent().getSelectedCursor().isEnabled()) {
            BUTTON.render(context, mouseX, mouseY, delta);
            renderRuler(context, mouseX, mouseY);
        } else {
            context.fill(getX(), getY(), getRight(), getBottom(), 0x7F000000); // 50% black overlay
        }

        context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0xFF000000);
        placeButton();
    }

    private void renderRuler(DrawContext context, int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            context.drawHorizontalLine(getX(), getRight() - 1, mouseY, HOTSPOT_RULER_COLOR);
            context.drawVerticalLine(mouseX, getY(), getBottom(), HOTSPOT_RULER_COLOR);
        }
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
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        // not supported
    }
}
