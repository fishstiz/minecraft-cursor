package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.gui.widget.ContainerWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.CursorListWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorOptionsWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.List;

import static io.github.fishstiz.minecraftcursor.MinecraftCursorClient.CONFIG;

public class CursorOptionsScreen extends Screen {
    private static final Text TITLE_TEXT = Text.translatable("minecraft-cursor.options");
    private static final int CURSORS_COLUMN_WIDTH = 96;
    private static final int SELECTED_CURSOR_COLUMN_WIDTH = 200;
    private static final int COLUMN_GAP = 8;
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
        selectedCursor = cursorManager.getLoadedCursors().get(0);

        this.layout.addHeader(new TextWidget(this.title, this.textRenderer));
        this.body = this.layout.addBody(new CursorOptionsBody());

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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderBackgroundTexture(context);
        body.render(context, mouseX, mouseY, delta);
        moreButton.render(context, mouseX, mouseY, delta);
        doneButton.render(context, mouseX, mouseY, delta);
    }

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
        updateSelectedCursorConfig();
    }

    public void onChangeScale(double value) {
        if (selectedCursor.getScale() != value && body != null) {
            cursorManager.overrideCurrentCursor(selectedCursor.getType(), -1);
        }

        selectedCursor.setScale(value, this::reloadCursorOnUpdate);
        updateSelectedCursorConfig();
    }

    public void onChangeXHot(double value) {
        selectedCursor.setXhot((int) value, this::reloadCursorOnUpdate);
        updateSelectedCursorConfig();
    }

    public void onChangeYHot(double value) {
        selectedCursor.setYhot((int) value, this::reloadCursorOnUpdate);
        updateSelectedCursorConfig();
    }

    public void selectCursor(Cursor cursor) {
        updateSelectedCursorConfig();
        selectedCursor = cursor;

        if (body != null) {
            body.selectedCursorColumn.refreshWidgets();
        }
    }

    private void reloadCursorOnUpdate() {
        if (selectedCursor == cursorManager.getCurrentCursor()) {
            cursorManager.reloadCursor();
        }
    }

    private void updateSelectedCursorConfig() {
        CONFIG.getOrCreateCursorSettings(selectedCursor.getType()).update(
                selectedCursor.getScale(),
                selectedCursor.getXhot(),
                selectedCursor.getYhot(),
                selectedCursor.getEnabled()
        );
    }

    public Cursor getSelectedCursor() {
        return selectedCursor;
    }

    public List<Cursor> getCursors() {
        return cursors;
    }

    @Override
    public void close() {
        removeOverride();
        CONFIG.save();

        if (this.client != null) {
            this.client.setScreen(previousScreen);
        }
    }

    public int getContentHeight() {
        return height - layout.getHeaderHeight() - layout.getFooterHeight();
    }

    public void removeOverride() {
        cursorManager.removeOverride(-1);
    }

    public class CursorOptionsBody extends ContainerWidget {
        public final CursorListWidget cursorsColumn;
        public final SelectedCursorOptionsWidget selectedCursorColumn;

        public CursorOptionsBody() {
            super(layout.getX(), layout.getHeaderHeight(), CursorOptionsScreen.this.width, CursorOptionsScreen.this.getContentHeight(), Text.empty());
            cursorsColumn = new CursorListWidget(client, CURSORS_COLUMN_WIDTH, CursorOptionsScreen.this);
            selectedCursorColumn = new SelectedCursorOptionsWidget(SELECTED_CURSOR_COLUMN_WIDTH, CursorOptionsScreen.this);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            boolean isScrolled = super.mouseScrolled(mouseX, mouseY, amount);
            if (isScrolled && cursorsColumn.isMouseOver(mouseX, mouseY)) {
                cursorsColumn.mouseScrolled(mouseX, mouseY, amount);
            }
            return isScrolled;
        }

        @Override
        public List<? extends Element> children() {
            return List.of(cursorsColumn, selectedCursorColumn);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            cursorsColumn.render(context, mouseX, mouseY, delta);
            selectedCursorColumn.render(context, mouseX, mouseY, delta);
        }

        public void position(int width, ThreePartsLayoutWidget layout) {
            this.height = getContentsHeight();
            this.width = width;
            this.setPosition(this.getX(), layout.getHeaderHeight());

            cursorsColumn.setHeight(getContentHeight());
            selectedCursorColumn.setHeight(getContentHeight());

            int cursorsColumnX = width / 2 - CURSORS_COLUMN_WIDTH;
            int selectedCursorColumnX = width / 2;
            int leftShift = (selectedCursorColumnX + SELECTED_CURSOR_COLUMN_WIDTH) -
                    (((CURSORS_COLUMN_WIDTH + SELECTED_CURSOR_COLUMN_WIDTH) / 2) + (width / 2));

            cursorsColumn.setLeftPos(cursorsColumnX - leftShift - COLUMN_GAP / 2);
            selectedCursorColumn.setX(selectedCursorColumnX - leftShift + COLUMN_GAP / 2);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        }

        @Override
        protected int getContentsHeight() {
            return 0;
        }

        @Override
        protected double getDeltaYPerScroll() {
            return 0;
        }

        @Override
        protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
            this.render(context, mouseX, mouseY, delta);
        }
    }
}
