package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.Config;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.gui.CursorAnimationHelper;
import io.github.fishstiz.minecraftcursor.gui.MouseEvent;
import io.github.fishstiz.minecraftcursor.util.SettingsUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;

public class CursorHotspotWidget extends CursorWidget {
    private static final ResourceLocation BACKGROUND_64 = MinecraftCursor.loc("textures/gui/background_128.png");
    private static final int BACKGROUND_DISABLED = 0xAF000000; // 70% black
    private static final int RULER_COLOR = 0xFFFF0000; // red
    private static final int OVERRIDE_RULER_COLOR = 0xFF00FF00; // green
    private final Config.GlobalSettings global = CONFIG.getGlobal();
    private final CursorAnimationHelper animationHelper;
    private final SliderWidget xhotSlider;
    private final SliderWidget yhotSlider;
    private final @Nullable MouseEventListener mouseEventListener;
    private final int maxHotspot;
    private boolean renderRuler = true;
    private boolean dragging = false;

    public CursorHotspotWidget(
            @NotNull Cursor cursor,
            @NotNull CursorAnimationHelper animationHelper,
            @NotNull SliderWidget xhotSlider,
            @NotNull SliderWidget yhotSlider,
            @Nullable MouseEventListener mouseEventListener
    ) {
        super(CommonComponents.EMPTY, cursor, BACKGROUND_64);

        this.animationHelper = animationHelper;
        this.xhotSlider = xhotSlider;
        this.yhotSlider = yhotSlider;
        this.mouseEventListener = mouseEventListener;
        this.maxHotspot = SettingsUtil.getMaxHotspot(cursor);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.active = this.xhotSlider.isActive() || this.yhotSlider.isActive();
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBackground(@NotNull GuiGraphics guiGraphics) {
        super.renderBackground(guiGraphics);

        if (!this.active) {
            guiGraphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), BACKGROUND_DISABLED);
        }
    }

    @Override
    protected void renderCursor(@NotNull GuiGraphics guiGraphics, @NotNull Cursor cursor) {
        this.animationHelper.drawSprite(guiGraphics, cursor, this.getX(), this.getY(), this.getWidth());
    }

    @Override
    protected void renderRuler(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!this.renderRuler) {
            return;
        }

        boolean isGlobalX = this.global.isXHotActive();
        boolean isGlobalY = this.global.isYHotActive();

        int colorX = isGlobalX ? OVERRIDE_RULER_COLOR : RULER_COLOR;
        int colorY = isGlobalY ? OVERRIDE_RULER_COLOR : RULER_COLOR;

        int xhot = this.clampHotspot(isGlobalX ? this.global.getXHot() : (int) this.xhotSlider.getMappedValue());
        int yhot = this.clampHotspot(isGlobalY ? this.global.getYHot() : (int) this.yhotSlider.getMappedValue());

        float rulerSize = this.getCellSize();
        int xhotX1 = (int) ((getX() + xhot * rulerSize) - (rulerSize > 1 || xhot != this.maxHotspot ? 0 : 1));
        int xhotX2 = (int) ((getX() + xhot * rulerSize) + (xhot > 0 ? rulerSize : Math.max(rulerSize, 2)));
        int yhotY1 = (int) ((getY() + yhot * rulerSize) - (rulerSize > 1 || yhot != this.maxHotspot ? 0 : 1));
        int yhotY2 = (int) ((getY() + yhot * rulerSize) + (yhot > 0 ? rulerSize : Math.max(rulerSize, 2)));


        if ((isGlobalX && !isGlobalY) || (isGlobalX == isGlobalY)) {
            guiGraphics.fill(this.getX(), yhotY1, this.getRight(), yhotY2, colorY);
            guiGraphics.fill(xhotX1, this.getY(), xhotX2, this.getBottom(), colorX);
        } else {
            guiGraphics.fill(xhotX1, this.getY(), xhotX2, this.getBottom(), colorX);
            guiGraphics.fill(this.getX(), yhotY1, this.getRight(), yhotY2, colorY);
        }
    }

    public void setRenderRuler(boolean renderRuler) {
        this.renderRuler = renderRuler;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.dragging = true;
        this.setHotspots(MouseEvent.CLICK, mouseX, mouseY);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (this.dragging) {
            this.setHotspots(MouseEvent.DRAG, mouseX, mouseY);
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        if (this.dragging) {
            this.dragging = false;
            this.setHotspots(MouseEvent.RELEASE, mouseX, mouseY);
            this.setFocused(false);
        }
    }

    public void setHotspots(MouseEvent mouseEvent, double mouseX, double mouseY) {
        float cellSize = this.getCellSize();
        int xhot = this.clampHotspot((int) ((mouseX - this.getX()) / cellSize));
        int yhot = this.clampHotspot((int) ((mouseY - this.getY()) / cellSize));

        if (this.xhotSlider.isActive()) {
            this.xhotSlider.applyMappedValue(xhot);
        }
        if (this.yhotSlider.isActive()) {
            this.yhotSlider.applyMappedValue(yhot);
        }
        if (this.mouseEventListener != null) {
            this.mouseEventListener.onMouseEvent(this, mouseEvent, xhot, yhot);
        }
    }

    private int clampHotspot(int hotspot) {
        return SettingsUtil.clamp(hotspot, SettingsUtil.HOT_MIN, this.maxHotspot);
    }

    @Override
    public CursorType getCursorType(double mouseX, double mouseY) {
        if (!this.active) {
            return CursorType.DEFAULT;
        }
        if (this.dragging) {
            return CursorType.GRABBING;
        }
        return CursorType.POINTER;
    }

    public interface MouseEventListener {
        void onMouseEvent(@NotNull CursorHotspotWidget target, @NotNull MouseEvent mouseEvent, int xhot, int yhot);
    }
}
