package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.util.DrawUtil;
import io.github.fishstiz.minecraftcursor.util.MouseEvent;
import io.github.fishstiz.minecraftcursor.util.SettingsUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;
import static io.github.fishstiz.minecraftcursor.MinecraftCursor.MOD_ID;

public class SelectedCursorHotspotWidget extends AbstractWidget implements CursorProvider {
    private static final ResourceLocation BACKGROUND_64 = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/background_64.png");
    private static final int BACKGROUND_DISABLED = 0xAF000000; // 70% black
    private static final int BORDER_COLOR = 0xFF000000; // black
    private static final int RULER_COLOR = 0xFFFF0000; // red
    private static final int OVERRIDE_RULER_COLOR = 0xFF00FF00; // green
    private final CursorConfig.GlobalSettings global = CONFIG.getGlobal();
    private final CursorOptionsWidget options;
    private boolean rulerRendered = true;
    private float rulerAlpha = 1f;
    private boolean dragging = false;
    private MouseEventListener changeEventListener;

    public SelectedCursorHotspotWidget(int size, CursorOptionsWidget options) {
        super(options.getX(), options.getY(), size, size, Component.empty());
        this.options = options;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        DrawUtil.drawCheckerboard(context, getX(), getY(), getWidth(), getHeight(), getCellSize(), BACKGROUND_64, 64);
        if (!active) context.fill(getX(), getY(), getRight(), getBottom(), BACKGROUND_DISABLED);

        renderCursor(context);
        renderRuler(context, mouseX, mouseY);
        context.renderOutline(getX(), getY(), getWidth(), getHeight(), BORDER_COLOR);
    }

    private void renderCursor(GuiGraphics context) {
        options.parent().animationHelper.drawSprite(context, options.parent().getSelectedCursor(), getX(), getY(), width);
    }

    private void renderRuler(GuiGraphics context, int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY)) setRulerRendered(true, false);

        rulerAlpha = Mth.lerp(0.3f, rulerAlpha, rulerRendered ? 1f : 0f);

        if (rulerAlpha <= 0.01f) return;

        boolean isGlobalX = global.isXHotActive();
        boolean isGlobalY = global.isYHotActive();

        int alpha = (int) (rulerAlpha * 255);
        int blendedColorX = getBlendedColor(isGlobalX ? OVERRIDE_RULER_COLOR : RULER_COLOR, alpha);
        int blendedColorY = getBlendedColor(isGlobalY ? OVERRIDE_RULER_COLOR : RULER_COLOR, alpha);

        int xhot = clampHotspot(isGlobalX ? global.getXHot() : (int) options.xhotSlider.getTranslatedValue());
        int yhot = clampHotspot(isGlobalY ? global.getYHot() : (int) options.yhotSlider.getTranslatedValue());

        float rulerSize = this.getCellSize();
        int xhotX1 = (int) (getX() + xhot * rulerSize);
        int xhotX2 = (int) ((getX() + xhot * rulerSize) + (xhot > 0 ? rulerSize : Math.max(rulerSize, 2)));
        int yhotY1 = (int) (getY() + yhot * rulerSize);
        int yhotY2 = (int) ((getY() + yhot * rulerSize) + (yhot > 0 ? rulerSize : Math.max(rulerSize, 2)));

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
        this.dragging = true;
        setHotspots(MouseEvent.CLICK, mouseX, mouseY);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        setHotspots(MouseEvent.DRAG, mouseX, mouseY);
    }

    public void setHotspots(MouseEvent mouseEvent, double mouseX, double mouseY) {
        if (changeEventListener != null) {
            float cellSize = getCellSize();
            int x = clampHotspot((int) ((mouseX - getX()) / cellSize));
            int y = clampHotspot((int) ((mouseY - getY()) / cellSize));

            changeEventListener.onChange(mouseEvent, x, y);
        }

        setRulerRendered(true, true);
    }

    private float getCellSize() {
        return (float) getWidth() / this.options.parent().getSelectedCursor().getTextureWidth();
    }

    private int clampHotspot(int hotspot) {
        return SettingsUtil.sanitizeHotspot(hotspot, options.parent().getSelectedCursor());
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
        if (this.dragging) {
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
    public void onRelease(double mouseX, double mouseY) {
        if (this.dragging) {
            setHotspots(MouseEvent.RELEASE, mouseX, mouseY);
            this.dragging = false;
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput builder) {
        this.defaultButtonNarrationText(builder);
    }

    @FunctionalInterface
    public interface MouseEventListener {
        void onChange(MouseEvent mouseEvent, int xhot, int yhot);
    }
}
