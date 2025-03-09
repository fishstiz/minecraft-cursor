package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.util.DrawUtil;
import io.github.fishstiz.minecraftcursor.util.MouseEvent;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;
import static io.github.fishstiz.minecraftcursor.gui.widget.CursorOptionsWidget.*;

public class MoreOptionsListWidget extends ContainerObjectSelectionList<MoreOptionsListWidget.OptionEntry> {
    private static final CursorConfig.GlobalSettings GLOBAL = CONFIG.getGlobal();

    private static final String GLOBAL_TOOLTIP_KEY = "minecraft-cursor.options.more.global.tooltip";
    private static final Component GLOBAL_SETTINGS_TEXT = Component.translatable("minecraft-cursor.options.more.global");

    private static final Tooltip ANIMATION_TOOLTIP = Tooltip.create(Component.translatable("minecraft-cursor.options.more.animation.tooltip"));
    private static final Component ANIMATION_TEXT = Component.translatable("minecraft-cursor.options.more.animation");

    private static final Tooltip ADAPTIVE_CURSOR_TOOLTIP = Tooltip.create(Component.translatable("minecraft-cursor.options.more.adapt.tooltip"));
    private static final Component ADAPTIVE_CURSOR_TEXT = Component.translatable("minecraft-cursor.options.more.adapt");
    private static final Component ITEM_SLOT_TEXT = Component.translatable("minecraft-cursor.options.item_slot");
    private static final Component ITEM_GRAB_TEXT = Component.translatable("minecraft-cursor.options.item_grab");
    private static final Component CREATIVE_TABS_TEXT = Component.translatable("minecraft-cursor.options.creative_tabs");
    private static final Component ENCHANTMENTS_TEXT = Component.translatable("minecraft-cursor.options.enchantments");
    private static final Component STONECUTTER_TEXT = Component.translatable("minecraft-cursor.options.stonecutter");
    private static final Component BOOK_EDIT_TEXT = Component.translatable("minecraft-cursor.options.book_edit");
    private static final Component LOOM_TEXT = Component.translatable("minecraft-cursor.options.loom");
    private static final Component ADVANCEMENTS_TEXT = Component.translatable("minecraft-cursor.options.advancements");
    private static final Component WORLD_ICON_TEXT = Component.translatable("minecraft-cursor.options.world");
    private static final Component SERVER_ICON_TEXT = Component.translatable("minecraft-cursor.options.server");

    private static final Component RESOURCE_TEXT = Component.translatable("minecraft-cursor.options.more.resource_pack");
    private static final Component RESOURCE_RELOAD_TEXT = Component.translatable("minecraft-cursor.options.more.resource_pack.reset");
    private static final Component RESOURCE_RELOAD_TOOLTIP_TEXT = Component.translatable("minecraft-cursor.options.more.resource_pack.reset.tooltip");

    private static final Component COMPAT_TEXT = Component.translatable("minecraft-cursor.options.more.compat");
    private static final Component REMAP_TEXT = Component.translatable("minecraft-cursor.options.more.compat.remap_cursors");
    private static final Tooltip REMAP_TOOLTIP = Tooltip.create(Component.translatable("minecraft-cursor.options.more.compat.remap_cursors.tooltip"));

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
    private boolean reloaded = false;

    public MoreOptionsListWidget(Minecraft client, int width, int height, int y, CursorManager cursorManager) {
        super(client, width, height, y, ITEM_HEIGHT + ROW_GAP);

        this.cursorManager = cursorManager;

        addGlobalOptions();
        addAdaptiveOptions();
        addModCompatOptions();
        addResourcePackOptions();
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

    private void addModCompatOptions() {
        addEntry(new TitleEntry(COMPAT_TEXT));
        addEntry(new ToggleEntry(REMAP_TEXT, CONFIG.isRemapCursorsEnabled(), true, REMAP_TOOLTIP, CONFIG::setRemapCursorsEnabled));
    }

    private void addResourcePackOptions() {
        addEntry(new TitleEntry(RESOURCE_TEXT));
        addEntry(new FullButtonEntry(RESOURCE_RELOAD_TEXT, RESOURCE_RELOAD_TOOLTIP_TEXT, this::reloadConfiguration));
    }

    private void reloadConfiguration() {
        CONFIG.set_hash(String.valueOf(Math.random()));
        minecraft.reloadResourcePacks().thenRun(() -> this.reloaded = true);
    }

    public boolean isReloaded() {
        return this.reloaded;
    }

    private void addAdaptiveEntry(Component label, boolean isEnabled, boolean active, Consumer<Boolean> onPress) {
        ToggleEntry entry = new ToggleEntry(label, isEnabled, active, onPress);
        adaptiveOptions.add(entry);
        this.addEntry(entry);
    }

    private SliderEntry createSliderEntry(
            Component text, String suffix,
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
            option.button.active = isEnabled;
            option.button.setValue(isEnabled);
        });

        cursorManager.setIsAdaptive(isEnabled);

        CONFIG.getSettings().forEach((key, settings) -> {
            if (key.equals(CursorType.DEFAULT.getKey())) return;

            settings.update(
                    settings.getScale(),
                    settings.getXHot(),
                    settings.getYHot(),
                    isEnabled
            );
        });
    }

    public int getYEntry(int index) {
        return getY() + itemHeight * index + ROW_GAP - (int) Math.round(getScrollAmount());
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
                getY() + itemHeight * getItemCount() + ROW_GAP,
                BUTTON_WIDTH,
                itemHeight - ROW_GAP,
                defaultValue,
                tooltip,
                onPress
        );
    }

    public static Tooltip getSettingTooltip(Component settingText) {
        return Tooltip.create(Component.translatable(GLOBAL_TOOLTIP_KEY, settingText));
    }

    public abstract static class OptionEntry extends Entry<OptionEntry> {
        protected final Component label;

        protected OptionEntry(Component label) {
            this.label = label;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return getChildren();
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return getChildren();
        }

        protected abstract List<AbstractWidget> getChildren();
    }

    public class LabeledButtonEntry<T extends AbstractWidget> extends OptionEntry {
        protected final T button;

        protected LabeledButtonEntry(Component label, Supplier<T> buttonFactory) {
            super(label);

            this.button = buttonFactory.get();
            this.button.setSize(BUTTON_WIDTH, 20);
        }

        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            button.setX(getRowRight() - BUTTON_WIDTH);
            button.setY(getYEntry(index));
            button.render(context, mouseX, mouseY, tickDelta);

            int textEndX = button.getX() - ROW_GAP;
            int textEndY = y + entryHeight;
            int textColor = 0xFFFFFFFF; // white
            DrawUtil.drawScrollableTextLeftAlign(context, minecraft.font, label, x, y, textEndX, textEndY, textColor);
        }

        @Override
        protected List<AbstractWidget> getChildren() {
            return List.of(button);
        }
    }

    public static class ToggleWidget extends SelectedCursorToggleWidget {
        protected ToggleWidget(
                int x, int y,
                int width, int height,
                boolean defaultValue,
                @Nullable Tooltip tooltip,
                Consumer<Boolean> onPress
        ) {
            super(x, y, width, height, Component.empty(), defaultValue, onPress);

            if (tooltip != null) setTooltip(tooltip);
        }

        @Override
        protected void updateMessage() {
            setMessage(value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
        }
    }

    public class TitleEntry extends OptionEntry {
        public TitleEntry(Component label) {
            super(Component.empty().append(label).withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW));
        }

        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int itemY = Math.round(y + entryHeight / 3.0f);
            int titleX = x + (getRowWidth() / 2 - minecraft.font.width(label) / 2);
            context.drawString(minecraft.font, label, titleX, itemY, 0xFFFFFFFF, false);
        }

        @Override
        protected List<AbstractWidget> getChildren() {
            return List.of();
        }
    }

    public class ToggleEntry extends LabeledButtonEntry<ToggleWidget> {
        public ToggleEntry(Component label, boolean defaultValue, boolean active, Consumer<Boolean> onPress) {
            this(label, defaultValue, active, null, onPress);
        }

        public ToggleEntry(
                Component label,
                boolean defaultValue,
                boolean active,
                @Nullable Tooltip tooltip,
                Consumer<Boolean> onPress
        ) {
            super(label, () -> createToggleWidget(defaultValue, tooltip, onPress));
            button.active = active;
        }
    }

    public class FullButtonEntry extends OptionEntry {
        private final SelectedCursorButtonWidget button;

        protected FullButtonEntry(Component label, Component tooltipText, Runnable onPress) {
            super(label);

            this.button = new SelectedCursorButtonWidget(label, onPress);
            this.button.setSize(getRowWidth(), 20);
            this.button.setTooltip(Tooltip.create(tooltipText));
        }

        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            button.setPosition(getRowLeft(), getYEntry(index));
            button.render(context, mouseX, mouseY, tickDelta);
        }

        @Override
        protected List<AbstractWidget> getChildren() {
            return List.of(button);
        }
    }

    public class SliderEntry extends ToggleEntry {
        private final SelectedCursorSliderWidget sliderWidget;

        public SliderEntry(Slider slider, Toggle toggle, Runnable onRelease) {
            super(Component.empty(), toggle.value, true, toggle.tooltip, toggle.toggleFunction);

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
        }

        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);

            sliderWidget.active = this.button.value;
            sliderWidget.setPosition(x - ROW_GAP / 2, getYEntry(index));
            sliderWidget.setSize(this.button.getX() - x - ROW_GAP, 20);
            sliderWidget.renderWidget(context, mouseX, mouseY, tickDelta);
        }

        @Override
        protected List<AbstractWidget> getChildren() {
            return List.of(sliderWidget, this.button);
        }

        public record Slider(Component label, String suffix, double value, double min, double max, double step,
                             Consumer<Double> applyFunction) {
        }

        public record Toggle(Component label, boolean value, Tooltip tooltip, Consumer<Boolean> toggleFunction) {
        }
    }
}
