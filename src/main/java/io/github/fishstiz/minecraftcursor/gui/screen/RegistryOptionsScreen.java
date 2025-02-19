package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorToggleWidget;
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

public class RegistryOptionsScreen extends Screen {
    private static final Tooltip ADAPTIVE_CURSOR_TOOLTIP = Tooltip.of(Text.translatable("minecraft-cursor.options.more.adapt.tooltip"));
    private static final Text ENABLED_TEXT = Text.translatable("minecraft-cursor.options.enabled");
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

    public void enableAll(boolean isEnabled) {
        body.options.forEach(option -> {
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

    public int getContentHeight() {
        return height - layout.getHeaderHeight() - layout.getFooterHeight();
    }

    @Override
    public List<? extends Element> children() {
        return List.of(body, doneButton);
    }

    public class RegistryListWidget extends ElementListWidget<RegistryListWidget.RegistryEntry> implements Widget {
        public final List<RegistryEntry> options = new ArrayList<>();

        public RegistryListWidget(MinecraftClient minecraftClient, RegistryOptionsScreen options) {
            super(
                    minecraftClient,
                    options.width,
                    options.getContentHeight(),
                    options.layout.getHeaderHeight(),
                    options.layout.getHeaderHeight() + options.getContentHeight(),
                    ITEM_HEIGHT + ROW_GAP
            );
            populateEntries();
        }

        public void populateEntries() {
            boolean isAdaptive = cursorManager.isAdaptive();

            this.addEntry(new RegistryEntry(ADAPTIVE_CURSOR_TEXT));
            this.addEntry(new RegistryEntry(
                    ENABLED_TEXT, isAdaptive, true, ADAPTIVE_CURSOR_TOOLTIP, RegistryOptionsScreen.this::enableAll));
            addOptionEntry(ITEM_SLOT_TEXT, CONFIG.isItemSlotEnabled(), isAdaptive, CONFIG::setItemSlotEnabled);
            addOptionEntry(ITEM_GRAB_TEXT, CONFIG.isItemGrabbingEnabled(), isAdaptive, CONFIG::setItemGrabbingEnabled);
            addOptionEntry(CREATIVE_TABS_TEXT, CONFIG.isCreativeTabsEnabled(), isAdaptive, CONFIG::setCreativeTabsEnabled);
            addOptionEntry(ENCHANTMENTS_TEXT, CONFIG.isEnchantmentsEnabled(), isAdaptive, CONFIG::setEnchantmentsEnabled);
            addOptionEntry(STONECUTTER_TEXT, CONFIG.isStonecutterRecipesEnabled(), isAdaptive, CONFIG::setStonecutterRecipesEnabled);
            addOptionEntry(BOOK_EDIT_TEXT, CONFIG.isBookEditEnabled(), isAdaptive, CONFIG::setBookEditEnabled);
            addOptionEntry(LOOM_TEXT, CONFIG.isLoomPatternsEnabled(), isAdaptive, CONFIG::setLoomPatternsEnabled);
            addOptionEntry(ADVANCEMENTS_TEXT, CONFIG.isAdvancementTabsEnabled(), isAdaptive, CONFIG::setAdvancementTabsEnabled);
            addOptionEntry(WORLD_ICON_TEXT, CONFIG.isWorldIconEnabled(), isAdaptive, CONFIG::setWorldIconEnabled);
            addOptionEntry(SERVER_ICON_TEXT, CONFIG.isServerIconEnabled(), isAdaptive, CONFIG::setServerIconEnabled);
        }

        public void addOptionEntry(Text label, boolean isEnabled, boolean defaultValue, Consumer<Boolean> onPress) {
            RegistryEntry entry = new RegistryEntry(label, isEnabled, defaultValue, onPress);
            options.add(entry);
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

        public class RegistryEntry extends Entry<RegistryEntry> {
            public final Text label;
            public RegistryToggleWidget toggleButton;

            public RegistryEntry(Text title) {
                this(Text.empty().append(title).formatted(Formatting.BOLD, Formatting.YELLOW), false, false, null);
            }

            public RegistryEntry(Text label, boolean defaultValue, boolean active, @Nullable Consumer<Boolean> onPress) {
                this(label, defaultValue, active, null, onPress);
            }

            public RegistryEntry(Text label, boolean defaultValue, boolean active, @Nullable Tooltip tooltip, @Nullable Consumer<Boolean> onPress) {
                this.label = label;
                if (onPress != null) {
                    toggleButton = new RegistryToggleWidget(
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
            }

            @Override
            public List<? extends Selectable> selectableChildren() {
                return toggleButton != null ? List.of(toggleButton) : List.of();
            }

            @Override
            public List<? extends Element> children() {
                return toggleButton != null ? List.of(toggleButton) : List.of();
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                int itemY = Math.round(y + entryHeight / 3.0f);
                if (toggleButton != null) {
                    context.drawText(textRenderer, label, x, itemY, 0xFFFFFFFF, false);
                    toggleButton.render(context, mouseX, mouseY, tickDelta);
                    toggleButton.setX(getRowRight() - BUTTON_WIDTH);
                    toggleButton.setY(layout.getHeaderHeight() + itemHeight * index + ROW_GAP - (int) Math.round(getScrollAmount()));
                    return;
                }
                int titleX = x + (getRowWidth() / 2 - textRenderer.getWidth(label) / 2);
                context.drawText(textRenderer, label, titleX, itemY, 0xFFFFFFFF, false);
            }
        }
    }

    public static class RegistryToggleWidget extends SelectedCursorToggleWidget {
        protected RegistryToggleWidget(int x, int y, int width, int height, boolean defaultValue, Tooltip tooltip, Consumer<Boolean> onPress) {
            super(x, y, width, height, Text.empty(), defaultValue, onPress);

            if (tooltip != null) setTooltip(tooltip);
        }

        @Override
        protected void updateMessage() {
            setMessage(value ? ScreenTexts.ON : ScreenTexts.OFF);
        }
    }
}
