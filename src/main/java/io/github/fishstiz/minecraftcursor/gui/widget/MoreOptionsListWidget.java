package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.util.DrawUtil;
import io.github.fishstiz.minecraftcursor.util.MouseEvent;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;
import static io.github.fishstiz.minecraftcursor.gui.widget.CursorOptionsWidget.*;
import static io.github.fishstiz.minecraftcursor.gui.widget.CursorOptionsWidget.ENABLED_TEXT;

public class MoreOptionsListWidget extends ElementListWidget<MoreOptionsListWidget.OptionEntry> implements Widget {
    private static final CursorConfig.GlobalSettings GLOBAL = CONFIG.getGlobal();

    private static final String GLOBAL_TOOLTIP_KEY = "minecraft-cursor.options.more.global.tooltip";
    private static final Text GLOBAL_SETTINGS_TEXT = Text.translatable("minecraft-cursor.options.more.global");

    private static final Tooltip ANIMATION_TOOLTIP = Tooltip.of(Text.translatable("minecraft-cursor.options.more.animation.tooltip"));
    private static final Text ANIMATION_TEXT = Text.translatable("minecraft-cursor.options.more.animation");

    private static final Tooltip ADAPTIVE_CURSOR_TOOLTIP = Tooltip.of(Text.translatable("minecraft-cursor.options.more.adapt.tooltip"));
    private static final Text ADAPTIVE_CURSOR_TEXT = Text.translatable("minecraft-cursor.options.more.adapt");
    private static final Text ITEM_SLOT_TEXT = Text.translatable("minecraft-cursor.options.item_slot");
    private static final Text ITEM_GRAB_TEXT = Text.translatable("minecraft-cursor.options.item_grab");
    private static final Text CREATIVE_TABS_TEXT = Text.translatable("minecraft-cursor.options.creative_tabs");
    private static final Text ENCHANTMENTS_TEXT = Text.translatable("minecraft-cursor.options.enchantments");
    private static final Text STONECUTTER_TEXT = Text.translatable("minecraft-cursor.options.stonecutter");
    private static final Text BOOK_EDIT_TEXT = Text.translatable("minecraft-cursor.options.book_edit");
    private static final Text LOOM_TEXT = Text.translatable("minecraft-cursor.options.loom");
    private static final Text ADVANCEMENTS_TEXT = Text.translatable("minecraft-cursor.options.advancements");
    private static final Text WORLD_ICON_TEXT = Text.translatable("minecraft-cursor.options.world");
    private static final Text SERVER_ICON_TEXT = Text.translatable("minecraft-cursor.options.server");

    private static final int BUTTON_WIDTH = 40;
    private static final int ITEM_HEIGHT = 20;
    private static final int ROW_GAP = 6;

    private final CursorManager cursorManager;
    private final List<ToggleEntry> adaptiveOptions = new ArrayList<>();
    private final SliderEntry xhotEntry = createSliderEntry(XHOT_TEXT, "px",
            CursorConfig.Settings.Default.HOT_MIN, CursorConfig.Settings.Default.HOT_MAX, 1,
            GLOBAL::isXHotActive, GLOBAL::setXhotActive,
            GLOBAL::getXHot, GLOBAL::setXHotDouble,
            CursorConfig.Settings::getXHot, Cursor::setXHot
    );
    private final SliderEntry yhotEntry = createSliderEntry(YHOT_TEXT, "px",
            CursorConfig.Settings.Default.HOT_MIN, CursorConfig.Settings.Default.HOT_MAX, 1,
            GLOBAL::isYHotActive, GLOBAL::setYhotActive,
            GLOBAL::getYHot, GLOBAL::setYHotDouble,
            CursorConfig.Settings::getYHot, Cursor::setYHot
    );
    private int y;

    public MoreOptionsListWidget(MinecraftClient minecraftClient, int width, int height, int y, int bottom, CursorManager cursorManager) {
        super(minecraftClient, width, height, y, bottom, ITEM_HEIGHT + ROW_GAP);

        this.cursorManager = cursorManager;
        this.y = y;

        addGlobalOptions();
        addAdaptiveOptions();
    }

    private void addGlobalOptions() {
        addEntry(new TitleEntry(GLOBAL_SETTINGS_TEXT));
        addEntry(new ToggleEntry(
                ANIMATION_TEXT,
                cursorManager.isAnimated(),
                cursorManager.hasAnimations(),
                ANIMATION_TOOLTIP,
                this::toggleAnimations)
        );

        addEntry(createSliderEntry(SCALE_TEXT, "",
                CursorConfig.Settings.Default.SCALE_MIN, CursorConfig.Settings.Default.SCALE_MAX, CursorConfig.Settings.Default.SCALE_STEP,
                GLOBAL::isScaleActive, GLOBAL::setScaleActive,
                GLOBAL::getScale, GLOBAL::setScale,
                CursorConfig.Settings::getScale, Cursor::setScale
        ));

        addEntry(xhotEntry);
        addEntry(yhotEntry);
    }

    private void addAdaptiveOptions() {
        boolean isAdaptive = cursorManager.isAdaptive();
        addEntry(new TitleEntry(ADAPTIVE_CURSOR_TEXT));
        addEntry(new ToggleEntry(ENABLED_TEXT, isAdaptive, true, ADAPTIVE_CURSOR_TOOLTIP, this::toggleAdaptive));
        addAdaptiveEntry(ITEM_SLOT_TEXT, CONFIG.isItemSlotEnabled(), isAdaptive, CONFIG::setItemSlotEnabled);
        addAdaptiveEntry(ITEM_GRAB_TEXT, CONFIG.isItemGrabbingEnabled(), isAdaptive, CONFIG::setItemGrabbingEnabled);
        addAdaptiveEntry(CREATIVE_TABS_TEXT, CONFIG.isCreativeTabsEnabled(), isAdaptive, CONFIG::setCreativeTabsEnabled);
        addAdaptiveEntry(ENCHANTMENTS_TEXT, CONFIG.isEnchantmentsEnabled(), isAdaptive, CONFIG::setEnchantmentsEnabled);
        addAdaptiveEntry(STONECUTTER_TEXT, CONFIG.isStonecutterRecipesEnabled(), isAdaptive, CONFIG::setStonecutterRecipesEnabled);
        addAdaptiveEntry(BOOK_EDIT_TEXT, CONFIG.isBookEditEnabled(), isAdaptive, CONFIG::setBookEditEnabled);
        addAdaptiveEntry(LOOM_TEXT, CONFIG.isLoomPatternsEnabled(), isAdaptive, CONFIG::setLoomPatternsEnabled);
        addAdaptiveEntry(ADVANCEMENTS_TEXT, CONFIG.isAdvancementTabsEnabled(), isAdaptive, CONFIG::setAdvancementTabsEnabled);
        addAdaptiveEntry(WORLD_ICON_TEXT, CONFIG.isWorldIconEnabled(), isAdaptive, CONFIG::setWorldIconEnabled);
        addAdaptiveEntry(SERVER_ICON_TEXT, CONFIG.isServerIconEnabled(), isAdaptive, CONFIG::setServerIconEnabled);
    }

    private void addAdaptiveEntry(Text label, boolean isEnabled, boolean active, Consumer<Boolean> onPress) {
        ToggleEntry entry = new ToggleEntry(label, isEnabled, active, onPress);
        adaptiveOptions.add(entry);
        this.addEntry(entry);
    }

    private SliderEntry createSliderEntry(
            Text text, String suffix,
            double min, double max, double step,
            BooleanSupplier activeGetter,
            BooleanConsumer activeSetter,
            DoubleSupplier valueGetter,
            DoubleConsumer valueSetter,
            ToDoubleFunction<CursorConfig.Settings> settingsValueGetter,
            ObjDoubleConsumer<Cursor> cursorAction
    ) {
        Runnable updateCursors = () -> cursorManager.getLoadedCursors().forEach(cursor -> {
            double value = activeGetter.getAsBoolean()
                    ? valueGetter.getAsDouble()
                    : settingsValueGetter.applyAsDouble(CONFIG.getOrCreateCursorSettings(cursor.getType()));
            cursorAction.accept(cursor, value);
        });
        DoubleConsumer handleChange = value -> {
            valueSetter.accept(value);
            cursorAction.accept(cursorManager.getCurrentCursor(), value);
        };
        BooleanConsumer handleToggle = active -> {
            activeSetter.accept(active);
            updateCursors.run();
        };

        var slider = new SliderEntry.Slider(text, suffix, valueGetter.getAsDouble(), min, max, step, handleChange::accept);
        var toggle = new SliderEntry.Toggle(ENABLED_TEXT, activeGetter.getAsBoolean(), getSettingTooltip(text), handleToggle);
        return new SliderEntry(slider, toggle, updateCursors);
    }

    public void handleChangeHotspotWidget(MouseEvent mouseEvent, int xhot, int yhot) {
        boolean applyX = GLOBAL.isXHotActive();
        boolean applyY = GLOBAL.isYHotActive();

        if (applyX) {
            GLOBAL.setXHot(xhot);
            xhotEntry.sliderWidget.setTranslatedValue(xhot);
        }
        if (applyY) {
            GLOBAL.setYHot(yhot);
            yhotEntry.sliderWidget.setTranslatedValue(yhot);
        }

        if (applyX && applyY) {
            cursorManager.getCurrentCursor().setHotspots(xhot, yhot);
        } else if (applyX) {
            cursorManager.getCurrentCursor().setXHot(xhot);
        } else if (applyY) {
            cursorManager.getCurrentCursor().setYHot(yhot);
        }

        if (mouseEvent == MouseEvent.RELEASE) {
            applyHotspotsToAll();
        }
    }

    private void applyHotspotsToAll() {
        boolean applyX = GLOBAL.isXHotActive();
        boolean applyY = GLOBAL.isYHotActive();

        if (applyX && applyY) {
            cursorManager.getLoadedCursors().forEach(cursor -> cursor.setHotspots(GLOBAL.getXHot(), GLOBAL.getYHot()));
        } else if (applyX) {
            cursorManager.getLoadedCursors().forEach(cursor -> cursor.setXHot(GLOBAL.getXHot()));
        } else if (applyY) {
            cursorManager.getLoadedCursors().forEach(cursor -> cursor.setYHot(GLOBAL.getYHot()));
        }
    }

    private void applyScaleToAll() {
        if (GLOBAL.isScaleActive()) {
            cursorManager.getLoadedCursors().forEach(cursor -> cursor.setScale(GLOBAL.getScale()));
        }
    }

    // in case the user escapes the screen before releasing slider.
    public void applyConfig() {
        applyScaleToAll();
        applyHotspotsToAll();
    }

    private void toggleAnimations(boolean isAnimated) {
        cursorManager.setIsAnimated(isAnimated);

        CONFIG.getSettings().forEach((key, settings) -> {
            if (cursorManager.getCursor(key) instanceof AnimatedCursor) {
                settings.setAnimated(isAnimated);
            }
        });
    }

    private void toggleAdaptive(boolean isEnabled) {
        adaptiveOptions.forEach(option -> {
            option.toggleButton.active = isEnabled;
            option.toggleButton.setValue(isEnabled);
        });

        cursorManager.setIsAdaptive(isEnabled);

        CONFIG.getSettings().forEach((key, settings) -> settings.update(
                settings.getScale(),
                settings.getXHot(),
                settings.getYHot(),
                isEnabled
        ));
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public int getYEntry(int index) {
        return getY() + getItemHeight() * index + ROW_GAP - (int) Math.round(getScrollAmount());
    }

    public int getRowGap() {
        return ROW_GAP;
    }

    public boolean isEditingHotspot() {
        return (GLOBAL.isXHotActive() || GLOBAL.isYHotActive()) && (xhotEntry.isFocused() || yhotEntry.isFocused());
    }

    public ToggleWidget createToggleWidget(boolean defaultValue, Tooltip tooltip, Consumer<Boolean> onPress) {
        return new ToggleWidget(
                getRowRight() - BUTTON_WIDTH,
                getY() + itemHeight * getEntryCount() + ROW_GAP,
                BUTTON_WIDTH,
                itemHeight - ROW_GAP,
                defaultValue,
                tooltip,
                onPress
        );
    }

    public static Tooltip getSettingTooltip(Text settingText) {
        return Tooltip.of(Text.translatable(GLOBAL_TOOLTIP_KEY, settingText));
    }

    public void position(int width, int height, int y) {
        this.width = width;
        this.height = height;
        this.setLeftPos(0);
        this.top = y;
    }


    @Override
    public void setX(int x) {
        this.setLeftPos(x);
    }

    @Override
    public void setY(int y) {
        // unsupported
    }

    @Override
    public int getX() {
        return getRowLeft();
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return this.getRowWidth();
    }

    @Override
    public int getHeight() {
        return this.itemHeight;
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
        // unsupported
    }

    public abstract static class OptionEntry extends ElementListWidget.Entry<OptionEntry> {
        protected final Text label;

        protected OptionEntry(Text label) {
            this.label = label;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return getChildren();
        }

        @Override
        public List<? extends Element> children() {
            return getChildren();
        }

        protected List<ClickableWidget> getChildren() {
            return List.of();
        }
    }

    public class ToggleWidget extends SelectedCursorToggleWidget {
        protected ToggleWidget(
                int x, int y,
                int width, int height,
                boolean defaultValue,
                @Nullable Tooltip tooltip,
                Consumer<Boolean> onPress
        ) {
            super(x, y, width, height, Text.empty(), defaultValue, onPress);

            if (tooltip != null) setTooltip(tooltip);
        }

        public void setIndex(int index) {
            super.setX(getRowRight() - BUTTON_WIDTH);
            super.setY(getYEntry(index));
        }

        @Override
        protected void updateMessage() {
            setMessage(value ? ScreenTexts.ON : ScreenTexts.OFF);
        }
    }

    public class TitleEntry extends OptionEntry {
        public TitleEntry(Text label) {
            super(Text.empty().append(label).formatted(Formatting.BOLD, Formatting.YELLOW));
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int itemY = Math.round(y + entryHeight / 3.0f);
            int titleX = x + (getRowWidth() / 2 - client.textRenderer.getWidth(label) / 2);
            context.drawText(client.textRenderer, label, titleX, itemY, 0xFFFFFFFF, false);
        }
    }

    public class ToggleEntry extends OptionEntry {
        private final ToggleWidget toggleButton;

        public ToggleEntry(Text label, boolean defaultValue, boolean active, Consumer<Boolean> onPress) {
            this(label, defaultValue, active, null, onPress);
        }

        public ToggleEntry(
                Text label,
                boolean defaultValue,
                boolean active,
                @Nullable Tooltip tooltip,
                Consumer<Boolean> onPress
        ) {
            super(label);

            toggleButton = createToggleWidget(defaultValue, tooltip, onPress);
            toggleButton.active = active;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            toggleButton.setIndex(index);
            toggleButton.render(context, mouseX, mouseY, tickDelta);

            int textEndX = toggleButton.getX() - ROW_GAP;
            int textEndY = y + entryHeight;
            int textColor = 0xFFFFFFFF;
            DrawUtil.drawScrollableTextLeftAlign(context, client.textRenderer, label, x, y, textEndX, textEndY, textColor);
        }

        @Override
        protected List<ClickableWidget> getChildren() {
            return List.of(toggleButton);
        }
    }

    public class SliderEntry extends OptionEntry {
        private final SelectedCursorSliderWidget sliderWidget;
        private final ToggleWidget toggleButton;

        public SliderEntry(Slider slider, Toggle toggle, Runnable onRelease) {
            super(slider.label);

            sliderWidget = new SelectedCursorSliderWidget(
                    slider.label,
                    slider.value,
                    slider.min,
                    slider.max,
                    slider.step,
                    slider.suffix,
                    slider.applyFunction,
                    onRelease
            );
            sliderWidget.active = toggle.value;

            toggleButton = createToggleWidget(toggle.value, toggle.tooltip, handleToggle(toggle.toggleFunction));
        }

        private Consumer<Boolean> handleToggle(Consumer<Boolean> toggleFunction) {
            return active -> {
                sliderWidget.active = active;
                toggleFunction.accept(active);
            };
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            toggleButton.setIndex(index);
            toggleButton.render(context, mouseX, mouseY, tickDelta);

            sliderWidget.setPosition(x - ROW_GAP / 2, getYEntry(index));
            sliderWidget.setWidth(toggleButton.getX() - x - ROW_GAP);
            sliderWidget.height = 20;
            sliderWidget.render(context, mouseX, mouseY, tickDelta);
        }

        @Override
        protected List<ClickableWidget> getChildren() {
            return List.of(sliderWidget, toggleButton);
        }

        public record Slider(Text label, String suffix, double value, double min, double max, double step,
                             Consumer<Double> applyFunction) {
        }

        public record Toggle(Text label, boolean value, Tooltip tooltip, Consumer<Boolean> toggleFunction) {
        }
    }
}
