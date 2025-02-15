package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.gui.screen.CursorOptionsScreen;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class SelectedCursorHotspotWidget extends ClickableWidget implements CursorProvider {
    private static final Identifier BACKGROUND = Identifier.of(MinecraftCursor.MOD_ID, "textures/gui/hotspot_background.png");
    private static final int CURSOR_SIZE = 32;
    private static final int RULER_COLOR = 0xFFFF0000; // red
    private final SelectedCursorOptionsWidget optionsWidget;
    private boolean rulerRendered = true;
    private float rulerAlpha = 1f;

    public SelectedCursorHotspotWidget(int size, SelectedCursorOptionsWidget optionsWidget) {
        super(optionsWidget.getX(), optionsWidget.getY(), size, size, Text.empty());
        this.optionsWidget = optionsWidget;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(BACKGROUND, getX(), getY(), 0, 0, width, height, width, height);
        drawCursorTexture(context);
        renderRuler(context, mouseX, mouseY);
        context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0xFF000000);
    }

    private void drawCursorTexture(DrawContext context) {
        CursorOptionsScreen optionsScreen = optionsWidget.optionsScreen;
        Cursor cursor = optionsScreen.getSelectedCursor();
        int textureHeight = CURSOR_SIZE;
        int frameIndex = 0;

        if (cursor instanceof AnimatedCursor animatedCursor) {
            textureHeight *= animatedCursor.getFrameCount();
            frameIndex = optionsScreen.animationHelper.getCurrentFrame(animatedCursor);
        }

        int vOffset = CURSOR_SIZE * frameIndex;

        context.drawTexture(
                cursor.getSprite(),
                getX(), getY(),
                0, vOffset, // starting point
                width, height, // width/height to stretch/shrink
                CURSOR_SIZE, CURSOR_SIZE, // cropped width/height from actual image
                CURSOR_SIZE, textureHeight // actual width/height
        );
    }

    private void renderRuler(DrawContext context, int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY)) setRulerRendered(true, false);

        rulerAlpha = MathHelper.lerp(0.3f, rulerAlpha, rulerRendered ? 1f : 0f);

        if (rulerAlpha <= 0.01f) return;

        int xhot = (int) optionsWidget.xhotSlider.getTranslatedValue();
        int yhot = (int) optionsWidget.yhotSlider.getTranslatedValue();

        int rulerSize = getRulerSize();
        int xhotX1 = (getX() + xhot * rulerSize);
        int xhotX2 = (getX() + xhot * rulerSize) + rulerSize;
        int yhotY1 = (getY() + yhot * rulerSize);
        int yhotY2 = (getY() + yhot * rulerSize) + rulerSize;

        int alpha = (int) (rulerAlpha * 255);
        int blendedColor = (alpha << 24) | (RULER_COLOR & 0x00FFFFFF);

        context.fill(xhotX1, getY(), xhotX2, getBottom(), blendedColor);
        context.fill(getX(), yhotY1, getRight(), yhotY2, blendedColor);
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

        setRulerRendered(true, true);
    }

    private int getRulerSize() {
        return getWidth() / CURSOR_SIZE;
    }

    public void setRulerRendered(boolean rulerRendered, boolean immediate) {
        if (immediate) rulerAlpha = rulerRendered ? 1f : 0f;
        this.rulerRendered = rulerRendered;
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
