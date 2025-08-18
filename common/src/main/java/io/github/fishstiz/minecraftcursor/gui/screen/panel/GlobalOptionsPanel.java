package io.github.fishstiz.minecraftcursor.gui.screen.panel;

import io.github.fishstiz.minecraftcursor.CursorResourceLoader;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.gui.MouseEvent;
import io.github.fishstiz.minecraftcursor.gui.widget.ButtonWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.CursorPreviewWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.OptionsListWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.SliderWidget;
import io.github.fishstiz.minecraftcursor.util.SettingsUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;

public class GlobalOptionsPanel extends AbstractOptionsPanel {
    private static final Component TITLE = Component.translatable("minecraft-cursor.options.global.title");
    private static final Tooltip SCALE_TOOLTIP = createGlobalTooltip(SCALE_TEXT);
    private static final Tooltip XHOT_TOOLTIP = createGlobalTooltip(XHOT_TEXT);
    private static final Tooltip YHOT_TOOLTIP = createGlobalTooltip(YHOT_TEXT);
    private static final Component ANIMATIONS_TEXT = Component.translatable("minecraft-cursor.options.global.animation");
    private static final Tooltip ANIMATIONS_INFO = Tooltip.create(Component.translatable("minecraft-cursor.options.global.animation.tooltip"));
    private static final Component DEFERRED_LOADING_TEXT = Component.translatable("minecraft-cursor.options.global.deferred_loading");
    private static final Tooltip DEFERRED_LOADING_INFO = Tooltip.create(Component.translatable("minecraft-cursor.options.global.deferred_loading.tooltip"));
    private static final Component RESET_TEXT = Component.translatable("minecraft-cursor.options.resource_pack.reset");
    private static final Tooltip RESET_INFO = Tooltip.create(Component.translatable("minecraft-cursor.options.resource_pack.reset.tooltip"));
    private static final int PREVIEW_BUTTON_SIZE = 20;
    private static final int SCALE_OVERRIDE = -20;
    private final Runnable refreshCursors;
    private @NotNull Iterator<Cursor> cursors = cursorIterator();
    private @NotNull Cursor currentCursor = getDefaultCursor();
    private OptionsListWidget optionList;
    private GlobalPreviewWidget previewWidget;

    public GlobalOptionsPanel(Runnable refreshCursors) {
        super(TITLE);

        this.refreshCursors = refreshCursors;
    }

    @Override
    protected void initContents() {
        this.recycleCursors();
        final int maxHotspot = SettingsUtil.getMaxHotspot(CursorManager.INSTANCE.getCursors());

        this.previewWidget = new GlobalPreviewWidget(
                this.currentCursor,
                this.getFont(),
                new ButtonWidget(CommonComponents.EMPTY, this::cycleOnPreviewPress).withSize(PREVIEW_BUTTON_SIZE)
        );

        this.optionList = new OptionsListWidget(this.getMinecraft(), this.getFont(), Button.DEFAULT_HEIGHT, this.getSpacing());
        this.optionList.addToggleableSlider(
                new SliderWidget(
                        CONFIG.getGlobal().getScale(),
                        SettingsUtil.SCALE_MIN,
                        SettingsUtil.SCALE_MAX,
                        SettingsUtil.SCALE_STEP,
                        this::onChangeScale,
                        this.index(SCALE_TEXT),
                        CommonComponents.EMPTY,
                        SettingsUtil::getAutoText,
                        this::onSliderMouseEvent
                ),
                CONFIG.getGlobal().isScaleActive(),
                applyGlobalOnToggle(CONFIG.getGlobal()::setScaleActive),
                SCALE_TOOLTIP
        );
        this.optionList.addToggleableSlider(
                new SliderWidget(
                        CONFIG.getGlobal().getXHot(),
                        SettingsUtil.HOT_MIN,
                        maxHotspot,
                        SettingsUtil.HOT_STEP,
                        this::onChangeXHot,
                        this.index(XHOT_TEXT),
                        HOTSPOT_SUFFIX,
                        null,
                        this::onSliderMouseEvent
                ),
                CONFIG.getGlobal().isXHotActive(),
                applyGlobalOnToggle(CONFIG.getGlobal()::setXHotActive),
                XHOT_TOOLTIP
        );
        this.optionList.addToggleableSlider(
                new SliderWidget(
                        CONFIG.getGlobal().getYHot(),
                        SettingsUtil.HOT_MIN,
                        maxHotspot,
                        SettingsUtil.HOT_STEP,
                        this::onChangeYHot,
                        this.index(YHOT_TEXT),
                        HOTSPOT_SUFFIX,
                        null,
                        this::onSliderMouseEvent
                ),
                CONFIG.getGlobal().isYHotActive(),
                applyGlobalOnToggle(CONFIG.getGlobal()::setYHotActive),
                YHOT_TOOLTIP
        );
        this.optionList.addToggle(
                this.isAnimatedAny(),
                this::toggleCursorAnimations,
                this.index(ANIMATIONS_TEXT),
                ANIMATIONS_INFO,
                this.hasAnimationAny()
        );
        this.optionList.addToggle(
                CONFIG.isDeferredLoading(),
                CONFIG::setDeferredLoading,
                this.index(DEFERRED_LOADING_TEXT),
                DEFERRED_LOADING_INFO,
                true
        );
        this.optionList.addWidget(
                new ButtonWidget(
                        this.index(RESET_TEXT),
                        this::resetCursorSettings
                ).withTooltip(RESET_INFO)
        );

        this.optionList.search(this.getSearch());

        this.addRenderableWidget(this.previewWidget);
        this.addRenderableWidget(this.optionList);
    }

    @Override
    protected void repositionContents(int x, int y) {
        if (this.previewWidget != null && this.optionList != null) {
            this.previewWidget.setWidth(this.getWidth());
            this.previewWidget.setPosition(x, y);

            this.optionList.setSize(this.getWidth(), this.computeMaxHeight(y) - this.previewWidget.getHeight() - this.getSpacing());
            this.optionList.setPosition(x, this.previewWidget.getBottom() + this.getSpacing());
        }
    }

    private void onChangeScale(double scale) {
        CONFIG.getGlobal().setScale(scale);
        if (CONFIG.getGlobal().isScaleActive()) {
            this.currentCursor.setScale(scale);
        }
    }

    private void onChangeXHot(double xhot) {
        CONFIG.getGlobal().setXHot(xhot);
        if (CONFIG.getGlobal().isXHotActive()) {
            this.currentCursor.setXHot(xhot);
        }
    }

    private void onChangeYHot(double yhot) {
        CONFIG.getGlobal().setYHot(yhot);
        if (CONFIG.getGlobal().isYHotActive()) {
            this.currentCursor.setYHot(yhot);
        }
    }

    private void onSliderMouseEvent(SliderWidget target, MouseEvent mouseEvent, double scale) {
        if (mouseEvent.released()) {
            if (target.getPrefix().equals(SCALE_TEXT) && CONFIG.getGlobal().isScaleActive()) {
                CursorManager.INSTANCE.getCursors().forEach(cursor -> cursor.setScale(CONFIG.getGlobal().getScale()));
            } else if (target.getPrefix().equals(XHOT_TEXT) && CONFIG.getGlobal().isXHotActive()) {
                CursorManager.INSTANCE.getCursors().forEach(cursor -> cursor.setXHot(CONFIG.getGlobal().getXHot()));
            } else if (target.getPrefix().equals(YHOT_TEXT) && CONFIG.getGlobal().isYHotActive()) {
                CursorManager.INSTANCE.getCursors().forEach(cursor -> cursor.setYHot(CONFIG.getGlobal().getYHot()));
            }
            removeScaleOverride();
        } else if (mouseEvent.clicked() && target.getPrefix().equals(SCALE_TEXT) && CursorManager.INSTANCE.isEnabled(this.currentCursor)) {
            CursorController.getInstance().overrideCursor(this.currentCursor.getType(), SCALE_OVERRIDE);
        }
    }

    private void cycleOnPreviewPress(Button target) {
        target.setFocused(false);
        this.cycleCurrentCursor();
    }

    private void cycleCurrentCursor() {
        if (!this.cursors.hasNext()) {
            this.cursors = cursorIterator();
        }
        this.currentCursor = this.cursors.hasNext() ? this.cursors.next() : getDefaultCursor();

        if (this.previewWidget != null) {
            this.previewWidget.setCursor(this.currentCursor);
        }
    }

    private void recycleCursors() {
        this.cursors = cursorIterator();
        this.cycleCurrentCursor();
    }

    private void toggleCursorAnimations(boolean animated) {
        for (Cursor cursor : CursorManager.INSTANCE.getCursors()) {
            if (cursor instanceof AnimatedCursor animatedCursor) {
                animatedCursor.setAnimated(animated);
                CONFIG.getOrCreateSettings(animatedCursor).setAnimated(animated);
            }
        }
    }

    private void resetCursorSettings() {
        CursorResourceLoader.restoreResourceSettings();
        this.refreshCursors.run();
        this.refreshWidgets();
        this.repositionElements();
    }

    @Override
    protected void removed() {
        removeScaleOverride();
    }

    @Override
    protected void searched(@NotNull String search, @Nullable Component matched) {
        if (this.optionList != null) {
            this.optionList.search(search);
        }
    }

    public boolean hasAnimationAny() {
        for (Cursor cursor : CursorManager.INSTANCE.getCursors()) {
            if (cursor instanceof AnimatedCursor) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnimatedAny() {
        for (Cursor cursor : CursorManager.INSTANCE.getCursors()) {
            if (cursor instanceof AnimatedCursor animatedCursor && animatedCursor.isAnimated()) {
                return true;
            }
        }
        return false;
    }


    private static Consumer<Boolean> applyGlobalOnToggle(Consumer<Boolean> onToggle) {
        return value -> {
            onToggle.accept(value);
            CursorManager.INSTANCE.getCursors().forEach(cursor ->
                    cursor.apply(CONFIG.getGlobal().apply(CONFIG.getOrCreateSettings(cursor)))
            );
        };
    }

    private static Tooltip createGlobalTooltip(Component message) {
        return Tooltip.create(Component.translatable("minecraft-cursor.options.global.tooltip", message));
    }

    private static Iterator<Cursor> cursorIterator() {
        return CursorManager.INSTANCE.getCursors().stream().filter(Cursor::isEnabled).iterator();
    }

    private static @NotNull Cursor getDefaultCursor() {
        return Objects.requireNonNull(CursorManager.INSTANCE.getCursor(CursorType.DEFAULT));
    }

    public static void removeScaleOverride() {
        CursorController.getInstance().removeOverride(SCALE_OVERRIDE);
    }

    private static class GlobalPreviewWidget extends CursorPreviewWidget {
        private static final float CELL_DIVISOR = 32;
        private @NotNull Cursor cursor;

        public GlobalPreviewWidget(@NotNull Cursor cursor, @NotNull Font font, @Nullable Button button) {
            super(cursor, font, button);

            this.cursor = cursor;
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            this.renderBackground(guiGraphics);
            this.renderPreviewText(guiGraphics);
            this.renderTestButton(guiGraphics, mouseX, mouseY, partialTick);
            this.renderRuler(guiGraphics, mouseX, mouseY);
            this.renderBorder(guiGraphics);
        }

        @Override
        protected float getCellSize() {
            return this.getWidth() / CELL_DIVISOR;
        }

        public void setCursor(@NotNull Cursor cursor) {
            this.cursor = cursor;
        }

        @Override
        public @NotNull Cursor getCursor() {
            return this.cursor;
        }
    }
}
