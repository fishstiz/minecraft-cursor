package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorToggleWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
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

    protected RegistryOptionsScreen(Screen previousScreen, CursorManager cursorManager) {
        super(Text.translatable("minecraft-cursor.options.more"));
        this.previousScreen = previousScreen;
        this.cursorManager = cursorManager;
    }

    @Override
    protected void init() {
        this.layout.addHeader(this.title, this.textRenderer);
        this.body = this.layout.addBody(new RegistryListWidget(this.client, this));
        this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, btn -> this.close()).build());
        this.layout.forEachChild(this::addDrawableChild);
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        if (this.body != null) {
            this.layout.refreshPositions();
            this.body.position(width, layout);
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

    public class RegistryListWidget extends ElementListWidget<RegistryListWidget.RegistryEntry> {
        public final List<RegistryToggleEntry> adaptiveOptions = new ArrayList<>();

        public RegistryListWidget(MinecraftClient minecraftClient, RegistryOptionsScreen options) {
            super(minecraftClient, options.width, options.layout.getContentHeight(), options.layout.getHeaderHeight(), ITEM_HEIGHT + ROW_GAP);
            populateEntries();
        }

        public void toggleAdaptive(boolean isEnabled) {
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

        public void populateEntries() {
            boolean isAdaptive = cursorManager.isAdaptive();

            this.addEntry(new RegistryTitleEntry(ADAPTIVE_CURSOR_TEXT));
            this.addEntry(new RegistryToggleEntry(ENABLED_TEXT, isAdaptive, true, ADAPTIVE_CURSOR_TOOLTIP, this::toggleAdaptive));
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

        public void addAdaptiveEntry(Text label, boolean isEnabled, boolean active, Consumer<Boolean> onPress) {
            RegistryToggleEntry entry = new RegistryToggleEntry(label, isEnabled, active, onPress);
            adaptiveOptions.add(entry);
            this.addEntry(entry);
        }

        public abstract class RegistryEntry extends RegistryListWidget.Entry<RegistryEntry> {
            protected final Text label;

            protected RegistryEntry(Text label) {
                this.label = label;
            }

            @Override
            public List<? extends Selectable> selectableChildren() {
                return List.of();
            }

            @Override
            public List<? extends Element> children() {
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
            public List<? extends Selectable> selectableChildren() {
                return List.of(toggleButton);
            }

            @Override
            public List<? extends Element> children() {
                return List.of(toggleButton);
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                int itemY = Math.round(y + entryHeight / 3.0f);
                context.drawText(textRenderer, label, x, itemY, 0xFFFFFFFF, false);
                toggleButton.render(context, mouseX, mouseY, tickDelta);
                toggleButton.setX(getRowRight() - BUTTON_WIDTH);
                toggleButton.setY(layout.getHeaderHeight() + itemHeight * index + ROW_GAP - (int) Math.round(getScrollAmount()));
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

        @Override
        protected void updateMessage() {
            setMessage(value ? ScreenTexts.ON : ScreenTexts.OFF);
        }
    }
}
