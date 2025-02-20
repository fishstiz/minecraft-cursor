package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.gui.screen.CursorOptionsScreen;
import io.github.fishstiz.minecraftcursor.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class CursorListWidget extends ElementListWidget<CursorListWidget.CursorWidgetEntry> {
    private static final int ITEM_HEIGHT = 32;
    private static final int SCROLLBAR_OFFSET = 6;
    private static final int ROW_GAP = 1;
    private final List<CursorWidgetEntry> entries = new ArrayList<>();
    private final CursorOptionsScreen optionsScreen;

    public CursorListWidget(MinecraftClient client, int width, CursorOptionsScreen optionsScreen) {
        super(
                client,
                width + SCROLLBAR_OFFSET,
                optionsScreen.getContentHeight(),
                optionsScreen.layout.getHeaderHeight(),
                optionsScreen.layout.getHeaderHeight() + optionsScreen.getContentHeight(),
                ITEM_HEIGHT + ROW_GAP
        );
        this.top = optionsScreen.layout.getHeaderHeight();
        this.optionsScreen = optionsScreen;
        setRenderBackground(false);
        setRenderHeader(false, 0);
        setRenderHorizontalShadows(false);
        populateEntries();
    }

    public void populateEntries() {
        List<Cursor> cursors = optionsScreen.getCursors();
        for (Cursor cursor : cursors) {
            CursorWidgetEntry entry = new CursorWidgetEntry(
                    cursor,
                    optionsScreen.layout.getHeaderHeight() + itemHeight * getEntryCount(),
                    width - SCROLLBAR_OFFSET,
                    ITEM_HEIGHT,
                    client,
                    optionsScreen
            );
            entries.add(entry);
            this.addEntry(entry);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        CursorWidgetEntry entry = this.getEntryAtPosition(mouseX, mouseY);
        if (entry != null) {
            return entry.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected int getScrollbarPositionX() {
        return (left + width) - SCROLLBAR_OFFSET;
    }

    @Override
    public void setLeftPos(int leftPos) {
        this.left = leftPos;
        super.setLeftPos(leftPos);
        this.centerScrollOn(this.getFirst());

        for (CursorWidgetEntry entry : entries) {
            entry.button.setX(leftPos);
        }
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
        for (int i = 0; i < entries.size(); i++) {
            this.renderEntry(
                    context,
                    mouseX,
                    mouseY,
                    delta,
                    i,
                    this.getRowLeft(),
                    optionsScreen.layout.getHeaderHeight() + (itemHeight + ROW_GAP) * i - (int) Math.round(getScrollAmount()),
                    getRowWidth(),
                    itemHeight
            );
        }
    }

    public class CursorWidgetEntry extends ElementListWidget.Entry<CursorListWidget.CursorWidgetEntry> {
        public final CursorClickableWidget button;

        public CursorWidgetEntry(Cursor cursor, int y, int width, int height, MinecraftClient client, CursorOptionsScreen optionsScreen) {
            button = new CursorClickableWidget(0, y, width, height, cursor, client, optionsScreen);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
            button.render(context, mouseX, mouseY, delta);
            button.setY(optionsScreen.layout.getHeaderHeight() + (itemHeight + ROW_GAP) * index - (int) Math.round(getScrollAmount()));
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(button);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(button);
        }
    }

    public static class CursorClickableWidget extends PressableWidget {
        private static final String PREFIX_TEXT_KEY = "minecraft-cursor.options.cursor-type.";
        private static final int TEXTURE_SIZE = 16;
        private static final int PADDING_LEFT = 8;
        private static final int BACKGROUND_COLOR = 0x7F000000; // black 50%
        private static final int TEXT_COLOR = 0xFFFFFFFF; // white
        private static final int TEXT_DISABLED_COLOR = 0xFF555555; // dark gray
        private static final int BORDER_COLOR = 0xFFFFFFFF; // white
        private final MinecraftClient client;
        private final CursorOptionsScreen optionsScreen;
        private final Cursor cursor;

        public CursorClickableWidget(int x, int y, int width, int height, Cursor cursor, MinecraftClient client, CursorOptionsScreen optionsScreen) {
            super(x, y, width, height, Text.empty());
            this.client = client;
            this.optionsScreen = optionsScreen;
            this.cursor = cursor;
        }

        @Override
        public void onPress() {
            optionsScreen.selectCursor(cursor);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            renderBox(context);
            renderTexture(context);
            renderMessage(context);

            context.drawBorder(getX(), getY(), getWidth(), getHeight(),
                    isMouseOver(mouseX, mouseY) || cursor == optionsScreen.getSelectedCursor() ? BORDER_COLOR : 0xFF000000);
        }

        private void renderBox(DrawContext context) {
            int x2 = getX() + getWidth();
            int y2 = getY() + getHeight();
            context.fill(getX(), getY(), x2, y2, BACKGROUND_COLOR);
        }

        private void renderTexture(DrawContext context) {
            int x = getX() + PADDING_LEFT;
            int y = getY() + (getHeight() / 2) - (TEXTURE_SIZE / 2);
            optionsScreen.animationHelper.drawSprite(context, cursor, x, y, TEXTURE_SIZE);
        }

        private void renderMessage(DrawContext context) {
            Text message = Text.translatable(PREFIX_TEXT_KEY + this.cursor.getType().getKey());
            int color = cursor.isEnabled() ? TEXT_COLOR : TEXT_DISABLED_COLOR;
            int x = getX() + TEXTURE_SIZE + PADDING_LEFT * 2;
            int endX = (getX() + getWidth()) - SCROLLBAR_OFFSET;
            int endY = getY() + getHeight();

            DrawUtil.drawScrollableTextLeftAlign(context, client.textRenderer, message, x, getY(), endX, endY, color);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        }
    }
}
