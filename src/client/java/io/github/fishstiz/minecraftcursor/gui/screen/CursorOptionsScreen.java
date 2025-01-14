package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.gui.widget.CursorListWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorOptionsWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.List;

public class CursorOptionsScreen extends Screen {
    private final static Text TITLE_TEXT = Text.translatable("minecraft-cursor.options");
    public final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    protected CursorOptionsBody body;
    private Cursor selectedCursor;
    private final Screen previousScreen;
    private final CursorManager cursorManager;
    private final List<Cursor> cursors;

    public CursorOptionsScreen(Screen previousScreen, CursorManager cursorManager) {
        super(TITLE_TEXT);

        this.previousScreen = previousScreen;
        this.cursorManager = cursorManager;

        cursors = this.cursorManager.getLoadedCursors();
    }

    @Override
    protected void init() {
        selectedCursor = cursorManager.getLoadedCursors().getFirst();

        this.layout.addHeader(this.title, this.textRenderer);
        this.body = this.layout.addBody(new CursorOptionsBody(this));
        this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).width(200).build());
        this.layout.forEachChild(this::addDrawableChild);
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        if (this.body != null) {
            this.body.position(this.width, this.layout);
        }
    }

    public void onPressEnabled(ButtonWidget enableButton) {

    }

    public void onChangeScale(double value) {

    }

    public void onChangeXHot(double value) {

    }

    public void onChangeYHot(double value) {

    }

    public void selectCursor(Cursor cursor) {
        selectedCursor = cursor;
        this.body.selectedCursorColumn.refreshWidgets();
    }

    public Cursor getSelectedCursor() {
        return selectedCursor;
    }

    public List<Cursor> getCursors() {
        return cursors;
    }

    public CursorManager getCursorManager() {
        return cursorManager;
    }

    @Override
    public void close() {
        cursorManager.removeOverride(-1);

        if (this.client != null) {
            this.client.setScreen(previousScreen);
        }
    }

    public class CursorOptionsBody extends ContainerWidget {
        private static final int CURSORS_COLUMN_WIDTH = 96;
        private static final int SELECTED_CURSOR_COLUMN_WIDTH = 200;
        private static final int COLUMN_GAP = 6;
        public CursorListWidget cursorsColumn;
        public SelectedCursorOptionsWidget selectedCursorColumn;

        public CursorOptionsBody(CursorOptionsScreen optionsScreen) {
            super(optionsScreen.layout.getX(), optionsScreen.layout.getHeaderHeight(), optionsScreen.width, optionsScreen.layout.getContentHeight(), Text.of("BODY"));
            cursorsColumn = new CursorListWidget(client, CURSORS_COLUMN_WIDTH, optionsScreen);
            selectedCursorColumn = new SelectedCursorOptionsWidget(SELECTED_CURSOR_COLUMN_WIDTH, optionsScreen);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(cursorsColumn, selectedCursorColumn);
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            cursorsColumn.renderWidget(context, mouseX, mouseY, delta);
            selectedCursorColumn.renderWidget(context, mouseX, mouseY, delta);
        }

        public void position(int width, ThreePartsLayoutWidget layout) {
            this.setDimensions(width, layout.getContentHeight());
            this.setPosition(this.getX(), layout.getHeaderHeight());
            this.setHeight(layout.getContentHeight());

            cursorsColumn.setHeight(layout.getContentHeight());
            selectedCursorColumn.setHeight(layout.getContentHeight());

            int cursorsColumnX = width / 2 - CURSORS_COLUMN_WIDTH;
            int selectedCursorColumnX = width / 2;
            int leftShift = (selectedCursorColumnX + SELECTED_CURSOR_COLUMN_WIDTH) -
                    (((CURSORS_COLUMN_WIDTH + SELECTED_CURSOR_COLUMN_WIDTH) / 2) + (width / 2));

            cursorsColumn.setX(cursorsColumnX - leftShift - COLUMN_GAP / 2);
            selectedCursorColumn.setX(selectedCursorColumnX - leftShift + COLUMN_GAP / 2);
            this.refreshScroll();
        }

        @Override
        protected int getContentsHeightWithPadding() {
            return 0;
        }

        @Override
        protected double getDeltaYPerScroll() {
            return 0;
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        }
    }
}
