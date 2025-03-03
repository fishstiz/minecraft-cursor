package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import io.github.fishstiz.minecraftcursor.util.MouseEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.BiConsumer;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;

public class SelectedCursorHotspotWidget extends AbstractWidget implements CursorProvider {
    private static final CursorConfig.GlobalSettings global = CONFIG.getGlobal();
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(MinecraftCursor.MOD_ID, "textures/gui/hotspot_background.png");
    private static final int CURSOR_SIZE = 32;
    private static final int RULER_COLOR = 0xFFFF0000; // red
    private static final int OVERRIDE_RULER_COLOR = 0xFF00FF00; // green
    private final CursorOptionsWidget options;
    private boolean rulerRendered = true;
    private float rulerAlpha = 1f;
    private MouseEventListener changeEventListener;

    public SelectedCursorHotspotWidget(int size, CursorOptionsWidget options) {
        super(options.getX(), options.getY(), size, size, Component.empty());
        this.options = options;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.blit(RenderType::guiTextured, BACKGROUND, getX(), getY(), 0, 0, width, height, width, height);
        if (!active) context.fill(getX(), getY(), getRight(), getBottom(), 0xAF000000); // 70% black

        drawCursorTexture(context);
        renderRuler(context, mouseX, mouseY);
        context.renderOutline(getX(), getY(), getWidth(), getHeight(), 0xFF000000);
    }

    private void drawCursorTexture(GuiGraphics context) {
        options.parent().animationHelper.drawSprite(context, options.parent().getSelectedCursor(), getX(), getY(), width);
    }

    private void renderRuler(GuiGraphics context, int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY)) setRulerRendered(true, false);

        rulerAlpha = Mth.lerp(0.3f, rulerAlpha, rulerRendered ? 1f : 0f);

        if (rulerAlpha <= 0.01f) return;

        boolean isGlobalX = global.isXHotActive();
        boolean isGlobalY = global.isYHotActive();

        int xhot = isGlobalX ? global.getXHot() : (int) options.xhotSlider.getTranslatedValue();
        int yhot = isGlobalY ? global.getYHot() : (int) options.yhotSlider.getTranslatedValue();

        int rulerSize = getRulerSize();
        int xhotX1 = (getX() + xhot * rulerSize);
        int xhotX2 = (getX() + xhot * rulerSize) + rulerSize;
        int yhotY1 = (getY() + yhot * rulerSize);
        int yhotY2 = (getY() + yhot * rulerSize) + rulerSize;

        int alpha = (int) (rulerAlpha * 255);

        int blendedColorX = getBlendedColor(isGlobalX ? OVERRIDE_RULER_COLOR : RULER_COLOR, alpha);
        int blendedColorY = getBlendedColor(isGlobalY ? OVERRIDE_RULER_COLOR : RULER_COLOR, alpha);

        if ((isGlobalX && !isGlobalY) || (isGlobalX == isGlobalY)) {
            context.fill(getX(), yhotY1, getRight(), yhotY2, blendedColorY);
            context.fill(xhotX1, getY(), xhotX2, getBottom(), blendedColorX);
        } else {
            context.fill(xhotX1, getY(), xhotX2, getBottom(), blendedColorX);
            context.fill(getX(), yhotY1, getRight(), yhotY2, blendedColorY);
        }
    }

    public int getBlendedColor(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        setHotspots(MouseEvent.CLICK, mouseX, mouseY);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        setHotspots(MouseEvent.DRAG, mouseX, mouseY);
    }

    public void setHotspots(MouseEvent mouseEvent, double mouseX, double mouseY) {
        if (changeEventListener != null) {
            int rulerSize = getRulerSize();
            int x = ((int) mouseX - getX()) / rulerSize;
            int y = ((int) mouseY - getY()) / rulerSize;

            changeEventListener.onChange(mouseEvent, x, y);
        }

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
        if (!active) {
            return CursorType.DEFAULT;
        }
        if (isFocused() && (CursorTypeUtil.isLeftClickHeld() || CursorTypeUtil.isGrabbing())) {
            return CursorType.GRABBING;
        }
        return CursorType.POINTER;
    }

    public void setChangeEventListener(BiConsumer<Integer, Integer> changeEventListener) {
        setChangeEventListener((mouseEvent, x, y) -> changeEventListener.accept(x, y));
    }

    public void setChangeEventListener(MouseEventListener changeEventListener) {
        this.changeEventListener = changeEventListener;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible
                && mouseX >= this.getX()
                && mouseY >= this.getY()
                && mouseX < this.getRight()
                && mouseY < this.getBottom();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isFocused()) {
            setHotspots(MouseEvent.RELEASE, mouseX, mouseY);
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
        // not supported
    }

    @FunctionalInterface
    public interface MouseEventListener {
        void onChange(MouseEvent mouseEvent, int xhot, int yhot);
    }
}
