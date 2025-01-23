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
    private static final int CURSORS_COLUMN_WIDTH = 96;
    private static final int SELECTED_CURSOR_COLUMN_WIDTH = 200;
    private static final int COLUMN_GAP = 8;
    private final static Text TITLE_TEXT = Text.translatable("minecraft-cursor.options");
    private final CursorManager cursorManager;
    private final List<Cursor> cursors;
    protected final Screen previousScreen;
    public final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    protected CursorOptionsBody body;
    private Cursor selectedCursor;
    ButtonWidget moreButton;
    ButtonWidget doneButton;

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

        moreButton = ButtonWidget.builder(Text.translatable("minecraft-cursor.options.more").append("..."),
                btn -> {
                    assert client != null;
                    client.setScreen(new RegistryOptionsScreen(this, cursorManager));
                }).build();
        this.layout.addFooter(moreButton);

        doneButton = ButtonWidget.builder(ScreenTexts.DONE, btn -> this.close()).build();
        this.layout.addFooter(doneButton);

        this.layout.forEachChild(this::addDrawableChild);

        if (this.body != null) {
            this.refreshWidgetPositions();
        }
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        if (this.body != null) {
            this.body.position(this.width, this.layout);
        }
        int bodyWidth = CURSORS_COLUMN_WIDTH + SELECTED_CURSOR_COLUMN_WIDTH + COLUMN_GAP;
        moreButton.setWidth(bodyWidth / 2 - COLUMN_GAP / 2);
        moreButton.setX(width / 2 - moreButton.getWidth() - COLUMN_GAP / 2);
        doneButton.setWidth(bodyWidth / 2 - COLUMN_GAP / 2 - 2);
        doneButton.setX(width / 2 + COLUMN_GAP / 2);
    }

    public void onPressEnabled(boolean value) {
        selectedCursor.enable(value);
    }

    public void onChangeScale(double value) {
        if (selectedCursor.getScale() != value && body != null) {
            cursorManager.overrideCurrentCursor(selectedCursor.getType(), -1);
        }

        selectedCursor.setScale(value, this::onUpdate);
    }

    public void onChangeXHot(double value) {
        selectedCursor.setXhot((int) value, this::onUpdate);
    }

    public void onChangeYHot(double value) {
        selectedCursor.setYhot((int) value, this::onUpdate);
    }

    public void selectCursor(Cursor cursor) {
        cursorManager.saveCursor(selectedCursor.getType());
        selectedCursor = cursor;

        if (body != null) {
            body.selectedCursorColumn.refreshWidgets();
        }
    }

    private void onUpdate() {
        if (selectedCursor == cursorManager.getCurrentCursor()) {
            cursorManager.reloadCursor();
        }
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
        removeOverride();
        cursorManager.saveCursor(selectedCursor.getType());

        if (this.client != null) {
            this.client.setScreen(previousScreen);
        }
    }

    public void removeOverride() {
        cursorManager.clearOverrides();
    }

    public class CursorOptionsBody extends ContainerWidget {
        public CursorListWidget cursorsColumn;
        public SelectedCursorOptionsWidget selectedCursorColumn;

        public CursorOptionsBody(CursorOptionsScreen optionsScreen) {
            super(optionsScreen.layout.getX(), optionsScreen.layout.getHeaderHeight(), optionsScreen.width, optionsScreen.layout.getContentHeight(), Text.of("BODY"));
            cursorsColumn = new CursorListWidget(client, CURSORS_COLUMN_WIDTH, optionsScreen);
            selectedCursorColumn = new SelectedCursorOptionsWidget(SELECTED_CURSOR_COLUMN_WIDTH, optionsScreen);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
            boolean isScrolled = super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            if (isScrolled && cursorsColumn.isMouseOver(mouseX, mouseY)) {
                cursorsColumn.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            }
            return isScrolled;
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
