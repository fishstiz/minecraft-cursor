package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorToggleWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;


public class RegistryOptionsScreen extends Screen {
    private final static Text ADAPTIVE_CURSOR_TEXT = Text.translatable("minecraft-cursor.options.more.adapt");
    private static final Text CREATIVE_TABS_TEXT = Text.translatable("minecraft-cursor.options.creative_tabs");
    private static final Text ENCHANTMENTS_TEXT = Text.translatable("minecraft-cursor.options.enchantments");
    private static final Text STONECUTTER_TEXT = Text.translatable("minecraft-cursor.options.stonecutter");
    private static final Text BOOK_EDIT_TEXT = Text.translatable("minecraft-cursor.options.book_edit");
    private static final Text LOOM_TEXT = Text.translatable("minecraft-cursor.options.loom");
    private static final Text WORLD_ICON_TEXT = Text.translatable("minecraft-cursor.options.world");
    private static final int BUTTON_WIDTH = 40;
    private static final int ITEM_HEIGHT = 20;
    private static final int ROW_GAP = 6;
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    private final Screen previousScreen;
    private final CursorConfig config = MinecraftCursorClient.CONFIG.get();
    private final Runnable save = MinecraftCursorClient.CONFIG::save;
    private RegistryListWidget body;

    protected RegistryOptionsScreen(Screen previousScreen) {
        super(Text.translatable("minecraft-cursor.options.more"));
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        this.layout.addHeader(this.title, this.textRenderer);
        this.body = this.layout.addBody(new RegistryListWidget(this.client, this));
        this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, btn -> this.close()).build());
        this.layout.forEachChild(this::addDrawableChild);
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        if (this.body != null) {
            this.layout.refreshPositions();
            this.body.position(width, layout);
        }
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(previousScreen);
        }
        save.run();
    }

    public class RegistryListWidget extends ElementListWidget<RegistryListWidget.RegistryEntry> {
        public RegistryListWidget(MinecraftClient minecraftClient, RegistryOptionsScreen options) {
            super(minecraftClient, options.width, options.layout.getContentHeight(), options.layout.getHeaderHeight(), ITEM_HEIGHT + ROW_GAP);
            populateEntries();
        }

        public void populateEntries() {
            this.addEntry(new RegistryEntry(ADAPTIVE_CURSOR_TEXT));
            this.addEntry(new RegistryEntry(CREATIVE_TABS_TEXT, config.isCreativeTabsEnabled(), config::setCreativeTabsEnabled));
            this.addEntry(new RegistryEntry(ENCHANTMENTS_TEXT, config.isEnchantmentsEnabled(), config::setEnchantmentsEnabled));
            this.addEntry(new RegistryEntry(STONECUTTER_TEXT, config.isStonecutterRecipesEnabled(), config::setStonecutterRecipesEnabled));
            this.addEntry(new RegistryEntry(BOOK_EDIT_TEXT, config.isBookEditEnabled(), config::setBookEditEnabled));
            this.addEntry(new RegistryEntry(LOOM_TEXT, config.isLoomPatternsEnabled(), config::setLoomPatternsEnabled));
            this.addEntry(new RegistryEntry(WORLD_ICON_TEXT, config.isWorldIconEnabled(), config::setWorldIconEnabled));
        }

        public class RegistryEntry extends ElementListWidget.Entry<RegistryListWidget.RegistryEntry> {
            public Text label;
            public RegistryToggleWidget toggleButton;

            public RegistryEntry(Text title) {
                this(Text.translatable(title.getString()).formatted(Formatting.BOLD, Formatting.YELLOW), false, null);
            }

            public RegistryEntry(Text label, boolean defaultValue, @Nullable Consumer<Boolean> onPress) {
                this.label = label;
                if (onPress != null) {
                    toggleButton = new RegistryToggleWidget(
                            getRowRight() - BUTTON_WIDTH,
                            layout.getHeaderHeight() + itemHeight * getEntryCount() + ROW_GAP,
                            BUTTON_WIDTH,
                            itemHeight - ROW_GAP,
                            defaultValue,
                            onPress
                    );
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
                    toggleButton.setY(layout.getHeaderHeight() + itemHeight * index + ROW_GAP - (int) Math.round(getScrollY()));
                    return;
                }
                int titleX = x + (getRowWidth() / 2 - textRenderer.getWidth(label) / 2);
                context.drawText(textRenderer, label, titleX, itemY, 0xFFFFFFFF, false);
            }
        }
    }

    public static class RegistryToggleWidget extends SelectedCursorToggleWidget {
        protected RegistryToggleWidget(int x, int y, int width, int height, boolean defaultValue, Consumer<Boolean> onPress) {
            super(x, y, width, height, Text.empty(), RegistryToggleWidget::onPressButton, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
            value = defaultValue;
            onPressConsumer = onPress;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            Text message = value ? ScreenTexts.ON : ScreenTexts.OFF;
            setMessage(message);
        }
    }
}
