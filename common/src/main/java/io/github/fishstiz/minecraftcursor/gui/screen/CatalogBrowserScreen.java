package io.github.fishstiz.minecraftcursor.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.gui.widget.AbstractListWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.ButtonWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.ElementView;
import io.github.fishstiz.minecraftcursor.gui.widget.ElementSlidingBackground;
import io.github.fishstiz.minecraftcursor.util.DrawUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public abstract class CatalogBrowserScreen extends Screen {
    private static final Component SEARCH_TEXT = Component.translatable("minecraft-cursor.options.search");
    private static final Tooltip CLEAR_SEARCH_INFO = Tooltip.create(Component.translatable("minecraft-cursor.options.search.clear"));
    private static final ResourceLocation EXIT_SPRITE = MinecraftCursor.loc("textures/gui/sprites/icon/exit.png");
    private static final ResourceLocation CLEAR_SPRITE = MinecraftCursor.loc("textures/gui/sprites/icon/cross.png");
    private final Screen previous;
    private final int headerHeight;
    private final int sidebarWidth;
    private final int maxContentWidth;
    private final int spacing;
    private final Map<CatalogItem, ItemContext> items = new LinkedHashMap<>();
    private EditBox searchField;
    private ButtonWidget clearButton;
    private ItemList catalog;
    private ButtonWidget doneButton;
    private ContentPanel contents;
    private String previousSearch = "";

    protected CatalogBrowserScreen(Component title, int headerHeight, int sidebarWidth, int maxContentWidth, int spacing, Screen previous) {
        super(title);

        this.headerHeight = headerHeight;
        this.sidebarWidth = sidebarWidth;
        this.maxContentWidth = maxContentWidth;
        this.spacing = spacing;
        this.previous = previous;
    }

    protected void initItems() {
    }

    protected void postInit() {
    }

    @Override
    protected final void init() {
        this.doneButton = this.addRenderableWidget(new ButtonWidget(CommonComponents.GUI_DONE, this::onClose)
                .withSize(Button.DEFAULT_HEIGHT)
                .withTooltip(CommonComponents.GUI_DONE)
                .spriteOnly(EXIT_SPRITE)
        );

        this.clearButton = new ButtonWidget(CommonComponents.EMPTY, this::clearSearch)
                .withSize(Button.DEFAULT_HEIGHT)
                .withTooltip(CLEAR_SEARCH_INFO)
                .spriteOnly(CLEAR_SPRITE);
        this.clearButton.active = false;

        this.searchField = this.addRenderableWidget(new EditBox(
                this.font,
                this.sidebarWidth - this.clearButton.getWidth() - this.spacing,
                this.headerHeight,
                SEARCH_TEXT
        ));
        this.searchField.setHint(SEARCH_TEXT);
        this.searchField.setResponder(this::search);

        this.addRenderableWidget(this.clearButton);

        this.catalog = this.addRenderableWidget(new ItemList(
                this.minecraft,
                this.font,
                this.sidebarWidth,
                this.spacing,
                this::onItemChange
        ));

        this.initItems();
        this.refreshItems();
        this.repositionElements();
        this.postInit();
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.previous);
        }
    }

    @Override
    public void removed() {
        if (this.contents != null) {
            this.contents.removed();
        }
    }

    @Override
    protected final void repositionElements() {
        int contentsWidth = this.getContentWidth();
        int usableWidth = this.sidebarWidth + this.spacing + contentsWidth;

        int maxOffsetX = this.width - usableWidth - this.spacing;
        int leftColumnX = Math.max(this.spacing, Math.min((this.width - usableWidth) / 2, maxOffsetX));
        int rightColumnX = leftColumnX + this.sidebarWidth + this.spacing;

        if (this.doneButton != null) {
            this.doneButton.setPosition(rightColumnX + contentsWidth - this.doneButton.getWidth(), this.spacing);
        }
        if (this.searchField != null && this.clearButton != null) {
            this.searchField.setPosition(leftColumnX, this.spacing);
            this.clearButton.setPosition(this.searchField.getRight() + this.spacing, this.spacing);
        }
        if (this.catalog != null) {
            this.catalog.setHeight(this.height - this.spacing * 2 - (this.headerHeight + this.spacing));
            this.catalog.setPosition(leftColumnX, this.spacing + this.headerHeight + this.spacing);
            this.catalog.clampScrollAmount();
        }
        if (this.contents != null) {
            this.contents.setWidth(contentsWidth);
            this.contents.setHeight(this.height - this.spacing * 2);
            this.contents.setPosition(rightColumnX, this.spacing);
            this.contents.repositionElements();
        }
    }

    protected int getContentWidth() {
        int totalWidth = this.width - this.spacing * 2;

        return this.maxContentWidth <= 0
                ? totalWidth - this.sidebarWidth - this.spacing
                : Math.min(this.maxContentWidth, totalWidth - this.sidebarWidth - this.spacing);
    }

    protected void selectItem(@Nullable CatalogItem item) {
        if (item != null) {
            this.catalog.select(item);
        } else {
            this.catalog.select((CatalogItem) null);
            if (this.contents != null) {
                this.removeWidget(this.contents);
                this.contents.removed();
                this.contents = null;
            }
        }
    }

    private void onItemChange(CatalogItem item) {
        ItemContext itemContext = this.items.get(item);

        if (itemContext == null) {
            throw new NullPointerException("CatalogItem " + item.id() + " context not found.");
        }

        ContentPanel contentPanel = itemContext.contents();
        if (contentPanel != this.contents) {
            if (this.contents != null) {
                this.removeWidget(this.contents);
                this.contents.removed();
            }

            this.contents = contentPanel;
            this.contents.changedItem(itemContext.category(), item);
            this.addRenderableWidget(this.contents);
            this.contents.added();
        } else {
            this.contents.changedItem(itemContext.category(), item);
        }

        this.repositionElements();
    }

    protected CatalogItem addCategory(@NotNull CatalogItem category, boolean collapsible, boolean collapsed) {
        this.catalog.addCategory(Objects.requireNonNull(category), new CategoryContext(collapsible, collapsed));
        return category;
    }

    protected CatalogItem addCategory(@NotNull CatalogItem category) {
        return this.addCategory(category, true, false);
    }

    protected void addCategoryOnly(@NotNull CatalogItem category, @NotNull ContentPanel panel) {
        this.addCategory(category, false, true);
        this.items.put(category, new ItemContext(category, this.initPanel(panel)));
    }

    protected void addItem(@NotNull CatalogItem category, @NotNull CatalogItem item, @NotNull ContentPanel panel) {
        if (this.items.containsKey(Objects.requireNonNull(category))) {
            throw new IllegalStateException("Category " + category.id() + " is already an item.");
        }

        this.items.put(Objects.requireNonNull(item), new ItemContext(category, this.initPanel(panel)));
        this.catalog.addItem(category, item);
    }

    protected void updateItem(@NotNull CatalogItem item, @NotNull ContentPanel contentPanel) {
        ItemContext oldContext = this.items.get(item);
        if (oldContext == null) {
            throw new IllegalStateException("CatalogItem " + item.id() + " has not beed added.");
        }

        ItemContext context = Objects.requireNonNull(this.items.computeIfPresent(item, (i, ctx) ->
                new ItemContext(ctx.category(), this.initPanel(contentPanel))
        ));

        this.items.replace(item, context);
        this.catalog.replaceItem(context.category(), item);

        if (this.contents != null && this.contents == oldContext.contents() && item.equals(this.contents.getItem())) {
            if (oldContext.contents() != context.contents()) {
                boolean isFocused = this.getFocused() == this.contents;

                this.removeWidget(this.contents);
                this.contents.removed();

                this.contents = context.contents();
                if (isFocused) this.setFocused(this.contents);

                this.contents.changedItem(context.category(), item);

                this.addRenderableWidget(this.contents);
                this.contents.added();
            } else {
                this.contents.changedItem(context.category(), item);
            }
            this.repositionElements();
        }
    }

    protected void addOrUpdateItem(@NotNull CatalogItem category, @NotNull CatalogItem item, @NotNull ContentPanel panel) {
        if (this.items.containsKey(item)) {
            this.updateItem(item, panel);
        } else {
            this.addItem(category, item, panel);
        }
    }

    protected void refreshItems() {
        this.catalog.refreshEntries();
    }

    private ContentPanel initPanel(ContentPanel panel) {
        panel.init(
                this.minecraft,
                this.font,
                this,
                this.headerHeight,
                this.getContentWidth() - this.doneButton.getWidth() - this.spacing,
                this.spacing
        );
        return panel;
    }

    protected void focusPath(GuiEventListener child) {
        this.clearFocus();
        ComponentPath.path(child, this).applyFocus(true);
    }

    protected boolean autoFocusSearch() {
        return true;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (super.charTyped(codePoint, modifiers)) {
            return true;
        }
        if (this.autoFocusSearch()
            && codePoint != InputConstants.KEY_SPACE
            && this.searchField != null
            && !this.searchField.isFocused()) {
            this.focusPath(this.searchField);
            return this.searchField.charTyped(codePoint, modifiers);
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.autoFocusSearch()
            && keyCode == InputConstants.KEY_BACKSPACE
            && this.searchField != null
            && !this.searchField.isFocused()
            && !this.searchField.getValue().isEmpty()) {
            this.focusPath(this.searchField);
            return this.searchField.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    private void clearSearch() {
        if (this.searchField != null) {
            this.searchField.setValue("");
        }
    }

    private void search(String search) {
        if (this.clearButton != null) {
            this.clearButton.active = !search.isEmpty();
        }
        if (Objects.equals(this.previousSearch, search)) {
            return;
        }

        this.previousSearch = search;
        if (search.isEmpty()) {
            this.catalog.filterItems(null);
            this.refreshItems();
            if (this.contents != null) {
                this.contents.searched(search, null);
            }
        } else {
            SearchResult result = this.performSearch(search.toLowerCase());
            this.catalog.filterItems(result.visibleItems());
            if (result.firstResult() != null) {
                this.selectItem(result.firstResult());
            }
            this.refreshItems();
            if (this.contents != null) {
                this.contents.searched(search, result.firstMatch());
            }
        }
    }

    private SearchResult performSearch(String lowercaseSearch) {
        Map<CatalogItem, Set<CatalogItem>> visibleItems = new HashMap<>();
        CatalogItem firstResult = null;
        Component firstMatch = null;

        for (Map.Entry<CatalogItem, ItemContext> itemEntry : this.items.entrySet()) {
            CatalogItem item = itemEntry.getKey();
            ItemContext context = itemEntry.getValue();

            MatchResult matchResult = this.findMatch(item, context, lowercaseSearch);
            if (matchResult.hasMatch()) {
                if (firstResult == null) {
                    firstResult = item;
                    firstMatch = matchResult.matchedText();
                }
                visibleItems.computeIfAbsent(context.category(), k -> new HashSet<>()).add(item);
            }
        }

        return new SearchResult(visibleItems, firstResult, firstMatch);
    }

    private MatchResult findMatch(CatalogItem item, ItemContext context, String lowercaseSearch) {
        Component matchedText = containsStringLowerCase(context.contents().indexed, lowercaseSearch);
        if (matchedText != null) {
            return new MatchResult(true, matchedText);
        }

        if (containsStringLowerCase(item.text(), lowercaseSearch)) {
            return new MatchResult(true, null);
        }

        if (containsStringLowerCase(context.category().text(), lowercaseSearch)) {
            return new MatchResult(true, null);
        }

        return new MatchResult(false, null);
    }

    private static boolean containsStringLowerCase(Component text, String string) {
        return text.plainCopy().getString().toLowerCase().contains(string);
    }

    private static @Nullable Component containsStringLowerCase(List<Component> texts, String string) {
        for (Component text : texts) {
            if (containsStringLowerCase(text, string)) {
                return text;
            }
        }
        return null;
    }

    private static class ItemList extends AbstractListWidget<ItemList.AbstractItemEntry> {
        private static final int UNPADDED_HEIGHT = 8;
        private final ElementSlidingBackground hoveredBackground = new ElementSlidingBackground(0x26FFFFFF); // 15% white
        private final ElementSlidingBackground selectedBackground = new ElementSlidingBackground(0x33FFFFFF); // 20% white
        private final ElementSlidingBackground focusedBackground = new ElementSlidingBackground(0xFFFFFFFF, true); // white
        private final Map<CatalogItem, CategoryContext> categories = new LinkedHashMap<>();
        private final Map<CatalogItem, Set<CatalogItem>> visibleItems = new HashMap<>();
        private final Consumer<CatalogItem> onSelect;
        private final Font font;
        private final int spacing;
        private boolean searching;
        private @Nullable CatalogItem selectedItem;

        private ItemList(Minecraft minecraft, Font font, int width, int spacing, Consumer<CatalogItem> onSelect) {
            super(minecraft, width, 0, 0, UNPADDED_HEIGHT + spacing * 2);

            this.onSelect = onSelect;
            this.font = font;
            this.spacing = spacing;
        }

        private void addCategory(CatalogItem category, CategoryContext context) {
            this.categories.put(category, context);
        }

        private void addItem(CatalogItem category, CatalogItem item) {
            CategoryContext context = this.categories.computeIfAbsent(category, c -> new CategoryContext());
            context.items().add(item);
        }

        private void replaceItem(@NotNull CatalogItem category, @NotNull CatalogItem item) {
            int index = this.indexOf(category, item);
            if (index == -1) {
                throw new IllegalStateException("CatalogItem " + item.id() + " has not beed added.");
            }
            this.categories.get(category).items().set(index, item);
        }

        private int indexOf(@NotNull CatalogItem category, @Nullable CatalogItem item) {
            CategoryContext context = this.categories.get(category);
            if (context != null) {
                List<CatalogItem> items = context.items();
                for (int i = 0; i < items.size(); i++) {
                    if (Objects.equals(items.get(i), item)) {
                        return i;
                    }
                }
            }
            return -1;
        }

        private void filterItems(@Nullable Map<CatalogItem, Set<CatalogItem>> visibleItems) {
            this.visibleItems.clear();
            this.searching = visibleItems != null;

            if (this.searching) {
                this.visibleItems.putAll(visibleItems);
            }
        }

        private void refreshEntries() {
            AbstractItemEntry focusedEntry = this.getFocused();
            AbstractItemEntry selectedEntry = this.getSelected();

            this.clearEntries();
            this.setFocused(null);

            for (Map.Entry<CatalogItem, CategoryContext> categoryContexts : this.categories.entrySet()) {
                if (this.searching && !this.visibleItems.containsKey(categoryContexts.getKey())) {
                    continue;
                }

                CategoryContext categoryContext = categoryContexts.getValue();

                CatalogItem categoryItem = categoryContexts.getKey()
                        .withText(text -> text.copy().withStyle(style -> style.withBold(true)))
                        .withPrefix(CategoryEntry.applyCollapsibleSymbol(categoryContext));

                CategoryEntry categoryEntry = new CategoryEntry(this.font, categoryItem, categoryContext.collapsible(), this.spacing);
                this.addEntry(categoryEntry);

                if (!categoryContext.collapsed()) {
                    Set<CatalogItem> items = this.visibleItems.get(categoryItem);
                    for (CatalogItem item : categoryContexts.getValue().items()) {
                        if (!this.searching || (items != null && (items.isEmpty() || items.contains(item)))) {
                            ItemEntry selectableEntry = new ItemEntry(this.font, item, this.spacing);
                            this.addEntry(selectableEntry);
                        }
                    }
                }
            }

            this.setFocused(focusedEntry);
            if (focusedEntry != selectedEntry) this.setSelected(selectedEntry);

            this.clampScrollAmount();
        }

        private void select(CatalogItem item, @Nullable AbstractItemEntry itemEntry) {
            if (this.selectedItem == null || !this.selectedItem.equals(item)) {
                this.selectedItem = item;
                this.setFocused(itemEntry);
                if (itemEntry != null) {
                    this.ensureVisible(itemEntry);
                }

                this.onSelect.accept(this.selectedItem);
            }
        }

        private void select(AbstractItemEntry itemEntry) {
            this.select(itemEntry.getItem(), itemEntry);
        }

        private void select(@Nullable CatalogItem item) {
            if (item != null) {
                this.select(item, this.getEntryFromItem(item));
            } else {
                this.selectedItem = null;
            }
        }

        private void toggleCategory(AbstractItemEntry itemEntry) {
            if (!(itemEntry instanceof CategoryEntry categoryEntry)) {
                throw new IllegalArgumentException("Entry is not an instance of CategoryEntry");
            }

            CatalogItem category = categoryEntry.getItem();
            CategoryContext context = this.categories.computeIfPresent(category, (c, ctx) -> ctx.toggle());

            this.setFocused(categoryEntry);

            this.refreshEntries();

            AbstractItemEntry focused = this.getFocused();
            if (focused != null) {
                focused.setFocused(focused.button);
            }

            if (context != null && !context.collapsed() && this.indexOf(category, this.selectedItem) == -1) {
                this.select(context.items().getFirst());
            }
        }

        private @Nullable AbstractItemEntry getEntryFromItem(@Nullable CatalogItem item) {
            if (item != null) {
                for (AbstractItemEntry entry : this.children()) {
                    if (entry.getItem().equals(item)) {
                        return entry;
                    }
                }
            }
            return null;
        }

        private void renderSlidingBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            this.hoveredBackground.render(guiGraphics, this.getEntryAtPosition(mouseX, mouseY), partialTick);
            this.selectedBackground.render(guiGraphics, this.getEntryFromItem(this.selectedItem), partialTick);
            this.focusedBackground.render(guiGraphics, this.getFocused(), partialTick);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
            this.selectedBackground.reset();
            this.focusedBackground.reset();
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }

        @Override
        public void renderListItems(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            this.renderSlidingBackground(guiGraphics, mouseX, mouseY, partialTick);
            super.renderListItems(guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        public void setFocused(@Nullable GuiEventListener focused) {
            if (focused instanceof AbstractItemEntry itemEntry) {
                focused = this.getEntryFromItem(itemEntry.getItem());
            }
            super.setFocused(focused);
        }

        @Override
        public void setSelected(@Nullable AbstractItemEntry selected) {
            if (selected != null) {
                selected = this.getEntryFromItem(selected.getItem());
            }
            super.setSelected(selected);
        }

        private abstract class AbstractItemEntry extends AbstractListWidget<AbstractItemEntry>.Entry implements ElementView {
            protected final Font font;
            protected final CatalogItem item;
            protected final ItemButton button;
            protected final List<ItemButton> children;

            protected AbstractItemEntry(Font font, CatalogItem item, int spacing, Consumer<AbstractItemEntry> onClick) {
                this.font = font;
                this.item = item;
                this.button = new ItemButton(this.item, this.font, spacing, itemButton -> onClick.accept(this));
                this.children = Collections.singletonList(this.button);
            }

            CatalogItem getItem() {
                return this.item;
            }

            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
                this.button.setSize(this.getWidth(), this.getHeight());
                this.button.setPosition(this.getX(), this.getY());
                this.button.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                GuiEventListener focusedElement = this.getFocused();
                if (focusedElement != null) {
                    focusedElement.setFocused(focused);
                }
            }

            @Override
            public @NotNull List<ItemButton> children() {
                return this.children;
            }

            @Override
            public @NotNull List<ItemButton> narratables() {
                return this.children;
            }
        }

        private class CategoryEntry extends AbstractItemEntry {
            private static final Component COLLAPSED_SYMBOL = wrapSymbol("▶");
            private static final Component COLLAPSIBLE_SYMBOL = wrapSymbol("▼");
            private static final Component NON_COLLAPSIBLE_SYMBOL = wrapSymbol("■");
            private static final int SYMBOL_COLOR = 0xFFFFFFFF; // white

            private CategoryEntry(Font font, CatalogItem item, boolean collapsible, int spacing) {
                super(font, item, spacing, collapsible ? ItemList.this::toggleCategory : ItemList.this::select);
            }

            private static Component wrapSymbol(String symbol) {
                return Component.literal(symbol).withStyle(style -> style.withBold(true));
            }

            private static UnaryOperator<CatalogItem.Prefix> applyCollapsibleSymbol(CategoryContext context) {
                Component symbol;
                if (!context.collapsible()) {
                    symbol = NON_COLLAPSIBLE_SYMBOL;
                } else if (context.collapsed()) {
                    symbol = COLLAPSED_SYMBOL;
                } else {
                    symbol = COLLAPSIBLE_SYMBOL;
                }

                return prefix -> {
                    if (prefix != null) return prefix;

                    return (guiGraphics, font, item, bounds, spacing, mouseX, mouseY, partialTick) -> {
                        int x = bounds.getX() + spacing;
                        int y = bounds.getY() + (bounds.getHeight() - font.lineHeight) / 2;
                        int width = Math.max(font.width(NON_COLLAPSIBLE_SYMBOL), Math.max(font.width(COLLAPSED_SYMBOL), font.width(COLLAPSIBLE_SYMBOL)));
                        int endX = x + width;
                        int endY = y + font.lineHeight;

                        DrawUtil.drawScrollableTextLeftAlign(guiGraphics, font, symbol, x, y, endX, endY, SYMBOL_COLOR);

                        return width;
                    };
                };
            }
        }

        private class ItemEntry extends AbstractItemEntry {
            private ItemEntry(Font font, CatalogItem item, int spacing) {
                super(font, item, spacing, ItemList.this::select);
            }
        }
    }

    private static class ItemButton extends AbstractButton {
        private static final int TEXT_COLOR = 0xFFFFFFFF; // white
        private final Consumer<ItemButton> onClick;
        private final CatalogItem item;
        private final Font font;
        private final int spacing;

        public ItemButton(CatalogItem item, Font font, int spacing, Consumer<ItemButton> onClick) {
            super(0, 0, 0, 0, item.text());

            this.font = font;
            this.item = item;
            this.spacing = spacing;
            this.onClick = Objects.requireNonNull(onClick);
        }

        @Override
        public void onPress() {
            this.onClick.accept(this);
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int prefixWidth = this.item.prefix() != null
                    ? this.item.prefix().render(guiGraphics, this.font, this.item, this, this.spacing, mouseX, mouseY, partialTick)
                    : 0;

            if (prefixWidth > 0) {
                prefixWidth += this.spacing;
            }

            int startX = this.getX() + this.spacing + prefixWidth;
            int startY = this.getY() + (this.getHeight() - this.font.lineHeight) / 2;
            int endX = this.getRight() - this.spacing;
            int endY = startY + this.font.lineHeight;

            DrawUtil.drawScrollableTextLeftAlign(guiGraphics, this.font, this.getMessage(), startX, startY, endX, endY, TEXT_COLOR);
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
            narrationElementOutput.add(NarratedElementType.TITLE, this.getMessage());
        }
    }

    public abstract static class ContentPanel extends AbstractContainerEventHandler implements Renderable, NarratableEntry, ElementView {
        private final List<Component> indexed = new ArrayList<>();
        private final List<GuiEventListener> children = new ArrayList<>();
        private final List<Renderable> renderables = new ArrayList<>();
        private final List<NarratableEntry> narratables = new ArrayList<>();
        private int x;
        private int y;
        private int width;
        private int height;
        private NarratableEntry lastNarratable;
        private CatalogItem item;
        private CatalogItem category;
        private boolean initialized;
        private int spacing;
        private int headerHeight;
        private int headerWidth;
        private Minecraft minecraft;
        private Font font;
        private CatalogBrowserScreen catalog;

        protected void added() {
        }

        protected void removed() {
        }

        protected void changed(@NotNull CatalogItem category, @NotNull CatalogItem item) {
        }

        protected void searched(@NotNull String search, @Nullable Component matched) {
        }

        protected void repositionElements() {
        }

        protected abstract void init();

        private void init(Minecraft minecraft, Font font, CatalogBrowserScreen catalog, int headerHeight, int headerWidth, int spacing) {
            if (!this.initialized) {
                this.initialized = true;
                this.minecraft = minecraft;
                this.font = font;
                this.catalog = catalog;
                this.spacing = spacing;
                this.headerHeight = headerHeight;
                this.headerWidth = headerWidth;
                this.init();
            }
        }

        private void changedItem(@NotNull CatalogItem category, @NotNull CatalogItem item) {
            this.category = Objects.requireNonNull(category);
            this.item = Objects.requireNonNull(item);
            this.changed(this.category, this.item);
        }

        protected void clearWidgets() {
            this.indexed.clear();
            this.children.clear();
            this.renderables.clear();
            this.narratables.clear();
        }

        protected Component index(Component text) {
            this.indexed.add(text);
            return text;
        }

        protected <T extends Renderable & GuiEventListener & NarratableEntry> void addRenderableWidget(T widget) {
            this.children.add(widget);
            this.narratables.add(widget);
            this.renderables.add(widget);
        }

        protected void addRenderableIndexedWidget(AbstractWidget widget) {
            this.addRenderableWidget(widget);
            this.indexed.add(widget.getMessage());
        }

        protected final void changeItem(CatalogItem item) {
            this.catalog.selectItem(item);
        }

        protected CatalogBrowserScreen getScreen() {
            return this.catalog;
        }

        protected Minecraft getMinecraft() {
            return this.minecraft;
        }

        protected Font getFont() {
            return this.font;
        }

        public CatalogItem getItem() {
            return this.item;
        }

        public CatalogItem getCategory() {
            return this.category;
        }

        @Override
        public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            for (Renderable renderable : this.renderables) {
                renderable.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth()
                   && mouseY >= this.getY() && mouseY < this.getY() + this.getHeight();
        }

        @Override
        public @NotNull List<GuiEventListener> children() {
            return this.children;
        }

        private void setPosition(int x, int y) {
            this.setX(x);
            this.setY(y);
        }

        private void setX(int x) {
            this.x = x;
        }

        private void setY(int y) {
            this.y = y;
        }

        @Override
        public int getX() {
            return this.x;
        }

        @Override
        public int getY() {
            return this.y;
        }

        private void setWidth(int width) {
            this.width = width;
        }

        @Override
        public int getWidth() {
            return this.width;
        }

        private void setHeight(int height) {
            this.height = height;
        }

        @Override
        public int getHeight() {
            return this.height;
        }

        public int getSpacing() {
            return this.spacing;
        }

        public int getHeaderHeight() {
            return this.headerHeight;
        }

        public int getHeaderWidth() {
            return this.headerWidth;
        }

        @Override
        public final @NotNull NarrationPriority narrationPriority() {
            return this.isFocused() ? NarrationPriority.FOCUSED : NarrationPriority.NONE;
        }

        @Override
        public final void updateNarration(@NotNull NarrationElementOutput narrationElementOutput) {
            List<NarratableEntry> sortedNarratables = this.narratables
                    .stream()
                    .filter(NarratableEntry::isActive)
                    .sorted(Comparator.comparingInt(TabOrderedElement::getTabOrderGroup))
                    .toList();
            NarratableSearchResult narratableSearchResult = findNarratableWidget(sortedNarratables, this.lastNarratable);
            if (narratableSearchResult != null) {
                if (narratableSearchResult.priority.isTerminal()) {
                    this.lastNarratable = narratableSearchResult.entry;
                }
                if (sortedNarratables.size() > 1 && narratableSearchResult.priority == NarrationPriority.FOCUSED) {
                    narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
                }
                narratableSearchResult.entry.updateNarration(narrationElementOutput.nest());
            }
        }
    }

    private record CategoryContext(boolean collapsible, boolean collapsed, @NotNull List<CatalogItem> items) {
        private CategoryContext {
            Objects.requireNonNull(items);
        }

        public CategoryContext(boolean collapsible, boolean collapsed) {
            this(collapsible, collapsed, new ArrayList<>());
        }

        public CategoryContext() {
            this(true, false, new ArrayList<>());
        }

        private CategoryContext toggle() {
            if (!this.collapsible) {
                throw new IllegalStateException("Cannot collapse non-collapsible category");
            }
            return new CategoryContext(true, !this.collapsed, this.items);
        }
    }

    private record ItemContext(@NotNull CatalogItem category, @NotNull ContentPanel contents) {
        private ItemContext {
            Objects.requireNonNull(category);
            Objects.requireNonNull(contents);
        }
    }

    private record SearchResult(
            Map<CatalogItem, Set<CatalogItem>> visibleItems,
            @Nullable CatalogItem firstResult,
            @Nullable Component firstMatch
    ) {
    }

    private record MatchResult(boolean hasMatch, Component matchedText) {
    }
}
