package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorSliderWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorToggleWidget;
import io.github.fishstiz.minecraftcursor.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;
import static io.github.fishstiz.minecraftcursor.config.CursorConfig.Settings.Default;

public class RegistryOptionsScreen extends Screen {
    private static final Text ENABLED_TEXT = Text.translatable("minecraft-cursor.options.enabled");

    private static final String GLOBAL_TOOLTIP_STRING = "minecraft-cursor.options.more.global.tooltip";
    private static final Text GLOBAL_SETTINGS_TEXT = Text.translatable("minecraft-cursor.options.more.global");
    private static final Text SCALE_TEXT = Text.translatable("minecraft-cursor.options.scale");

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
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    private final Screen previousScreen;
    private final CursorManager cursorManager;
    private RegistryListWidget body;
    private ButtonWidget doneButton;

    protected RegistryOptionsScreen(Screen previousScreen, CursorManager cursorManager) {
        super(Text.translatable("minecraft-cursor.options.more"));
        this.previousScreen = previousScreen;
        this.cursorManager = cursorManager;
    }

    @Override
    protected void init() {
        this.layout.addHeader(new TextWidget(this.title, this.textRenderer));
        this.body = this.layout.addBody(new RegistryListWidget(this.client, this));
        doneButton = ButtonWidget.builder(ScreenTexts.DONE, btn -> this.close()).build();
        this.layout.addFooter(doneButton);
        this.layout.forEachChild(this::addDrawableChild);
        this.refreshWidgetPositions();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderBackgroundTexture(context);
        body.render(context, mouseX, mouseY, delta);
        doneButton.render(context, mouseX, mouseY, delta);
    }

    protected void refreshWidgetPositions() {
        if (this.body != null) {
            this.layout.refreshPositions();
            this.body.position(width, getContentHeight(), layout.getHeaderHeight());
        }
    }

    @Override
    public void close() {
        if (this.client != null) {
            if (previousScreen instanceof CursorOptionsScreen screen && screen.body != null) {
                CursorOptionsScreen optionsScreen = new CursorOptionsScreen(screen.previousScreen, cursorManager);
                this.client.setScreen(optionsScreen);
                optionsScreen.selectCursor(screen.getSelectedCursor());
            } else {
                this.client.setScreen(previousScreen);
            }
        }
    }

    public int getContentHeight() {
        return height - layout.getHeaderHeight() - layout.getFooterHeight();
    }

    @Override
    public List<? extends Element> children() {
        return List.of(body, doneButton);
    }

    public class RegistryListWidget extends ElementListWidget<RegistryListWidget.RegistryEntry> implements Widget {
        private final List<RegistryToggleEntry> adaptiveOptions = new ArrayList<>();

        public RegistryListWidget(MinecraftClient minecraftClient, RegistryOptionsScreen options) {
            super(
                    minecraftClient,
                    options.width,
                    options.getContentHeight(),
                    options.layout.getHeaderHeight(),
                    options.layout.getHeaderHeight() + options.getContentHeight(),
                    ITEM_HEIGHT + ROW_GAP
            );
            addOptions();
        }

        public int getYOffset() {
            return layout.getHeaderHeight();
        }

        public int getItemHeight() {
            return itemHeight;
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

        private void applyGlobalScale(double scale) {

        }

        private void toggleGlobalScale(boolean enabled) {

        }

        private void addOptions() {
            addEntry(new RegistryTitleEntry(GLOBAL_SETTINGS_TEXT));

            addEntry(new RegistryToggleEntry(
                    ANIMATION_TEXT, cursorManager.isAnimated(), cursorManager.hasAnimations(),
                    ANIMATION_TOOLTIP, this::toggleAnimations));

            var slider = new RegistrySliderEntry.Slider(SCALE_TEXT, Default.SCALE, this::applyGlobalScale);
            var toggle = new RegistrySliderEntry.Toggle(ENABLED_TEXT, true, getSettingTooltip(SCALE_TEXT.getString()), this::toggleGlobalScale);
            addEntry(new RegistrySliderEntry(slider, toggle));


            boolean isAdaptive = cursorManager.isAdaptive();
            addEntry(new RegistryTitleEntry(ADAPTIVE_CURSOR_TEXT));
            addEntry(new RegistryToggleEntry(ENABLED_TEXT, isAdaptive, true, ADAPTIVE_CURSOR_TOOLTIP, this::toggleAdaptive));
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
            RegistryToggleEntry entry = new RegistryToggleEntry(label, isEnabled, active, onPress);
            adaptiveOptions.add(entry);
            this.addEntry(entry);
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
        }

        @Override
        public int getX() {
            return getRowLeft();
        }

        @Override
        public int getY() {
            return this.headerHeight;
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
        }

        public abstract class RegistryEntry extends RegistryListWidget.Entry<RegistryEntry> {
            protected final Text label;

            protected RegistryEntry(Text label) {
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

        public class RegistryTitleEntry extends RegistryEntry {
            public RegistryTitleEntry(Text label) {
                super(Text.empty().append(label).formatted(Formatting.BOLD, Formatting.YELLOW));
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                int itemY = Math.round(y + entryHeight / 3.0f);
                int titleX = x + (getRowWidth() / 2 - textRenderer.getWidth(label) / 2);
                context.drawText(textRenderer, label, titleX, itemY, 0xFFFFFFFF, false);
            }
        }

        public class RegistryToggleEntry extends RegistryEntry {
            private final ToggleWidget toggleButton;

            public RegistryToggleEntry(Text label, boolean defaultValue, boolean active, Consumer<Boolean> onPress) {
                this(label, defaultValue, active, null, onPress);
            }

            public RegistryToggleEntry(
                    Text label,
                    boolean defaultValue,
                    boolean active,
                    @Nullable Tooltip tooltip,
                    Consumer<Boolean> onPress
            ) {
                super(label);

                toggleButton = new ToggleWidget(
                        getRowRight() - BUTTON_WIDTH,
                        layout.getHeaderHeight() + itemHeight * getEntryCount() + ROW_GAP,
                        BUTTON_WIDTH,
                        itemHeight - ROW_GAP,
                        defaultValue,
                        tooltip,
                        onPress
                );
                toggleButton.active = active;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                toggleButton.setPosition(index, RegistryListWidget.this);
                toggleButton.render(context, mouseX, mouseY, tickDelta);

                int textEndX = toggleButton.getX() - ROW_GAP;
                int textEndY = y + entryHeight;
                int textColor = 0xFFFFFFFF;
                DrawUtil.drawScrollableTextLeftAlign(context, textRenderer, label, x, y, textEndX, textEndY, textColor);
            }

            @Override
            protected List<ClickableWidget> getChildren() {
                return List.of(toggleButton);
            }
        }

        public class RegistrySliderEntry extends RegistryEntry {
            private final SelectedCursorSliderWidget sliderWidget;
            private final ToggleWidget toggleButton;

            public RegistrySliderEntry(Slider slider, Toggle toggle) {
                super(slider.label);

                sliderWidget = new SelectedCursorSliderWidget(
                        slider.label,
                        slider.value,
                        Default.SCALE_MIN,
                        Default.SCALE_MAX,
                        Default.SCALE_STEP,
                        "px",
                        slider.applyFunction
                );

                toggleButton = new ToggleWidget(
                        getRowRight() - BUTTON_WIDTH,
                        layout.getHeaderHeight() + itemHeight * getEntryCount() + ROW_GAP,
                        BUTTON_WIDTH,
                        itemHeight - ROW_GAP,
                        toggle.value,
                        toggle.tooltip,
                        toggle.toggleFunction
                );
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                RegistryListWidget list = RegistryListWidget.this;

                toggleButton.setPosition(index, list);
                toggleButton.render(context, mouseX, mouseY, tickDelta);

                sliderWidget.setPosition(x - ROW_GAP / 2, getYEntry(index, list));
                sliderWidget.setDimensions(toggleButton.getX() - x - ROW_GAP, 20);
                sliderWidget.render(context, mouseX, mouseY, tickDelta);
            }

            @Override
            protected List<ClickableWidget> getChildren() {
                return List.of(sliderWidget, toggleButton);
            }

            public record Slider(Text label, double value, Consumer<Double> applyFunction) {
            }

            public record Toggle(Text label, boolean value, Tooltip tooltip, Consumer<Boolean> toggleFunction) {
            }
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
            super(x, y, width, height, Text.empty(), defaultValue, onPress);

            if (tooltip != null) setTooltip(tooltip);
        }

        public void setPosition(int index, RegistryListWidget list) {
            setX(list.getRowRight() - BUTTON_WIDTH);
            setY(getYEntry(index, list));
        }

        @Override
        protected void updateMessage() {
            setMessage(value ? ScreenTexts.ON : ScreenTexts.OFF);
        }
    }

    public static int getYEntry(int index, RegistryListWidget list) {
        return list.getYOffset() + list.getItemHeight() * index + ROW_GAP - (int) Math.round(list.getScrollAmount());
    }

    public static Tooltip getSettingTooltip(String setting) {
        return Tooltip.of(Text.translatable(GLOBAL_TOOLTIP_STRING, setting));
    }
}
