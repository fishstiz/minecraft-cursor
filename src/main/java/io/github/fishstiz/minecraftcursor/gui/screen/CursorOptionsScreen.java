package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.gui.widget.ContainerWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.CursorListWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.CursorOptionsHandler;
import io.github.fishstiz.minecraftcursor.gui.widget.CursorOptionsWidget;
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

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;

public class CursorOptionsScreen extends Screen {
    private static final Text TITLE_TEXT = Text.translatable("minecraft-cursor.options");
    private static final int CURSORS_COLUMN_WIDTH = 96;
    private static final int SELECTED_CURSOR_COLUMN_WIDTH = 200;
    private static final int COLUMN_GAP = 8;
    private final CursorManager cursorManager;
    private final List<Cursor> cursors;
    protected final Screen previousScreen;
    public final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    public final CursorAnimationHelper animationHelper = new CursorAnimationHelper();
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
        this.body = this.layout.addBody(new CursorOptionsBody(this));

        moreButton = ButtonWidget.builder(
                Text.translatable("minecraft-cursor.options.more").append("..."),
                btn -> toMoreOptions()).build();
        this.layout.addFooter(moreButton);

        doneButton = ButtonWidget.builder(ScreenTexts.DONE, btn -> this.close()).build();
        this.layout.addFooter(doneButton);

        this.layout.forEachChild(this::addDrawableChild);
        this.addDrawableChild(body.cursorsColumn);

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
        if (client != null) {
            client.setScreen(new MoreOptionsScreen(this, cursorManager));
        }
    }

    @Override
    public void close() {
        CursorOptionsHandler.removeScaleOverride();
        CONFIG.save();

        if (this.client != null) {
            this.client.setScreen(previousScreen);
        }
    }

    public int getContentHeight() {
        return height - layout.getHeaderHeight() - layout.getFooterHeight();
    }

    public class CursorOptionsBody extends ContainerWidget {
        public final CursorListWidget cursorsColumn;
        public final CursorOptionsWidget selectedCursorColumn;

        public CursorOptionsBody(CursorOptionsScreen optionsScreen) {
            super(optionsScreen.layout.getX(), optionsScreen.layout.getHeaderHeight(), optionsScreen.width, optionsScreen.getContentHeight(), Text.empty());
            cursorsColumn = new CursorListWidget(client, CURSORS_COLUMN_WIDTH, optionsScreen);
            selectedCursorColumn = new CursorOptionsWidget(SELECTED_CURSOR_COLUMN_WIDTH, optionsScreen);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            if (cursorsColumn.isMouseOver(mouseX, mouseY)) {
                return cursorsColumn.mouseScrolled(mouseX, mouseY, amount);
            }
            return super.mouseScrolled(mouseX, mouseY, amount);
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
