package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.gui.CursorAnimationHelper;
import io.github.fishstiz.minecraftcursor.gui.widget.AbstractContainerWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.CursorListWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.CursorOptionsHandler;
import io.github.fishstiz.minecraftcursor.gui.widget.CursorOptionsWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;

public class CursorOptionsScreen extends Screen {
    private static final Component TITLE_TEXT = Component.translatable("minecraft-cursor.options");
    private static final int CURSORS_COLUMN_WIDTH = 96;
    private static final int SELECTED_CURSOR_COLUMN_WIDTH = 200;
    private static final int COLUMN_GAP = 8;

    public final CursorAnimationHelper animationHelper = new CursorAnimationHelper();
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final Button moreButton = Button.builder(Component.translatable("minecraft-cursor.options.more").append("..."), btn -> toMoreOptions()).build();
    private final Button doneButton = Button.builder(CommonComponents.GUI_DONE, btn -> this.onClose()).build();
    private final List<Cursor> cursors;
    final Screen previousScreen;
    private Cursor selectedCursor;
    CursorOptionsBody body;

    public CursorOptionsScreen(Screen previousScreen) {
        super(TITLE_TEXT);

        this.previousScreen = previousScreen;

        cursors = CursorManager.INSTANCE.getLoadedCursors();
    }

    @Override
    protected void init() {
        selectedCursor = cursors.get(0);

        this.layout.addToHeader(new StringWidget(this.title, this.font));
        this.body = this.layout.addToContents(new CursorOptionsBody());

        this.layout.addToFooter(moreButton);
        this.layout.addToFooter(doneButton);

        this.layout.visitWidgets(this::addRenderableWidget);
        this.addRenderableWidget(body.cursorsColumn);

        if (this.body != null) {
            this.repositionElements();
        }
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderDirtBackground(context);
        body.render(context, mouseX, mouseY, delta);
        moreButton.render(context, mouseX, mouseY, delta);
        doneButton.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();

        int bodyWidth = CURSORS_COLUMN_WIDTH + SELECTED_CURSOR_COLUMN_WIDTH + COLUMN_GAP;
        int buttonWidth = bodyWidth / 2 - COLUMN_GAP / 2;
        int centerX = width / 2;
        int gapHalf = COLUMN_GAP / 2;

        moreButton.setWidth(buttonWidth);
        doneButton.setWidth(buttonWidth - 2);
        moreButton.setX(centerX - buttonWidth - gapHalf);
        doneButton.setX(centerX + gapHalf);

        body.position();
    }

    public void selectCursor(Cursor cursor) {
        updateSelectedCursorConfig();
        selectedCursor = cursor;

        if (body != null) {
            body.selectedCursorColumn.refresh();
        }
    }

    private void updateSelectedCursorConfig() {
        if (body != null) {
            body.selectedCursorColumn.save();
        }
    }

    public Cursor getSelectedCursor() {
        return selectedCursor;
    }

    public List<Cursor> getCursors() {
        return cursors;
    }

    public void toMoreOptions() {
        if (minecraft != null) {
            minecraft.setScreen(new MoreOptionsScreen(this));
        }
    }

    @Override
    public void onClose() {
        CursorOptionsHandler.removeScaleOverride();
        CONFIG.save();

        if (this.minecraft != null) {
            this.minecraft.setScreen(previousScreen);
        }
    }

    public int getContentHeight() {
        return height - layout.getHeaderHeight() - layout.getFooterHeight();
    }

    public class CursorOptionsBody extends AbstractContainerWidget {
        public final CursorListWidget cursorsColumn;
        public final CursorOptionsWidget selectedCursorColumn;
        private final List<GuiEventListener> children = new ArrayList<>();

        public CursorOptionsBody() {
            super(layout.getX(), layout.getHeaderHeight(), CursorOptionsScreen.this.width, getContentHeight(), Component.empty());

            int y = layout.getHeaderHeight();
            int height = getContentHeight();
            var screen = CursorOptionsScreen.this;

            cursorsColumn = new CursorListWidget(minecraft, CURSORS_COLUMN_WIDTH, height, y, y + height, screen);
            selectedCursorColumn = new CursorOptionsWidget(computedX2(), SELECTED_CURSOR_COLUMN_WIDTH, height, y, screen);
            this.children.add(cursorsColumn);
            this.children.add(selectedCursorColumn);

            position();
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            if (cursorsColumn.isMouseOver(mouseX, mouseY)) {
                return cursorsColumn.mouseScrolled(mouseX, mouseY, amount);
            }
            return super.mouseScrolled(mouseX, mouseY, amount);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
            cursorsColumn.setHeight(getInnerHeight());
            cursorsColumn.render(context, mouseX, mouseY, delta);
            selectedCursorColumn.render(context, mouseX, mouseY, delta);
        }

        public void position() {
            int width = CursorOptionsScreen.this.width;
            int height = getInnerHeight();
            int y = layout.getHeaderHeight();

            this.setDimensions(width, height);
            this.setPosition(0, y);

            cursorsColumn.setHeight(height);
            selectedCursorColumn.setHeight(height);

            cursorsColumn.setLeftPos(computedX());
            selectedCursorColumn.setX(computedX2());
        }

        private int computedX() {
            return CursorOptionsScreen.this.width / 2 - (CURSORS_COLUMN_WIDTH + SELECTED_CURSOR_COLUMN_WIDTH + COLUMN_GAP) / 2;
        }

        private int computedX2() {
            return computedX() + CURSORS_COLUMN_WIDTH + COLUMN_GAP;
        }

        public void setDimensions(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput builder) {
            // not supported
        }

        @Override
        protected int getInnerHeight() {
            return getContentHeight();
        }

        @Override
        protected double scrollRate() {
            return 0;
        }

        @Override
        protected void renderContents(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
            this.render(context, mouseX, mouseY, delta);
        }
    }
}
