package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SelectedCursorHotspotWidget extends SelectedCursorClickableWidget implements CursorProvider {
    private static final Identifier BACKGROUND = Identifier.of(MinecraftCursor.MOD_ID, "textures/cursors/hotspot_background.png");
    private static final int HOTSPOT_RULER_COLOR = 0xFFFF0000; // red
    private static final int CURSOR_SIZE = 32;
    private final SelectedCursorOptionsWidget optionsWidget;

    public SelectedCursorHotspotWidget(int size, SelectedCursorOptionsWidget optionsWidget) {
        super(optionsWidget.getX(), optionsWidget.getY(), size, size, Text.empty());
        this.optionsWidget = optionsWidget;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        drawTexture(context, BACKGROUND);
        drawTexture(context, optionsWidget.optionsScreen.getSelectedCursor().getSprite());
        renderRuler(context);
        context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0xFF000000);
    }

    private void drawTexture(DrawContext context, Identifier sprite) {
        context.drawTexture(sprite, getX(), getY(), 0, 0, width, height, width, height);
    }

    private void renderRuler(DrawContext context) {
        int xhot = (int) optionsWidget.xhotSlider.getTranslatedValue();
        int yhot = (int) optionsWidget.yhotSlider.getTranslatedValue();

        int rulerSize = getRulerSize();
        int xhotX1 = (getX() + xhot * rulerSize);
        int xhotX2 = (getX() + xhot * rulerSize) + rulerSize;
        int yhotY1 = (getY() + yhot * rulerSize);
        int yhotY2 = (getY() + yhot * rulerSize) + rulerSize;

        context.fill(xhotX1, getY(), xhotX2, getY() + getHeight(), HOTSPOT_RULER_COLOR);
        context.fill(getX(), yhotY1, getX() + getWidth(), yhotY2, HOTSPOT_RULER_COLOR);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        setHotspots(mouseX, mouseY);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        setHotspots(mouseX, mouseY);
    }

    public void setHotspots(double mouseX, double mouseY) {
        int rulerSize = getRulerSize();

        int xhot = ((int) mouseX - getX()) / rulerSize;
        int yhot = ((int) mouseY - getY()) / rulerSize;

        optionsWidget.xhotSlider.setValue(xhot);
        optionsWidget.yhotSlider.setValue(yhot);
    }

    private int getRulerSize() {
        return getWidth() / CURSOR_SIZE;
    }

    @Override
    public CursorType getCursorType(double mouseX, double mouseY) {
        if (CursorTypeUtil.isLeftClickHeld() || CursorTypeUtil.isGrabbing()) {
            return CursorType.GRABBING;
        }
        return CursorType.POINTER;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
