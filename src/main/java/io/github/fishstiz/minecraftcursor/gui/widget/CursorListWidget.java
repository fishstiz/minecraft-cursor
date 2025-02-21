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

import java.util.List;

public class CursorListWidget extends ElementListWidget<CursorListWidget.CursorEntry> {
    private static final int ITEM_HEIGHT = 32;
    private static final int SCROLLBAR_OFFSET = 6;
    private static final int ROW_GAP = 1;
    private final CursorOptionsScreen optionsScreen;

    public CursorListWidget(MinecraftClient client, int width, int height, int y, int bottom, CursorOptionsScreen optionsScreen) {
        super(client, width + SCROLLBAR_OFFSET, height, y, bottom, ITEM_HEIGHT + ROW_GAP);
        this.optionsScreen = optionsScreen;
        this.top = y;
        setRenderBackground(false);
        setRenderHeader(false, 0);
        setRenderHorizontalShadows(false);
        populateEntries();
    }

    public void populateEntries() {
        for (Cursor cursor : optionsScreen.getCursors()) {
            CursorEntry entry = new CursorEntry(
                    cursor,
                    left,
                    top + itemHeight * getEntryCount(),
                    width - SCROLLBAR_OFFSET,
                    ITEM_HEIGHT
            );
            this.addEntry(entry);
        }
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
    }

    public void setHeight(int height) {
        this.height = height;
    }

    class CursorEntry extends Entry<CursorEntry> {
        public final CursorButtonWidget button;

        public CursorEntry(Cursor cursor, int x, int y, int width, int height) {
            button = new CursorButtonWidget(x, y, width, height, cursor);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
            button.setX(CursorListWidget.this.left);
            button.setY(CursorListWidget.this.top + (itemHeight + ROW_GAP) * index - (int) Math.round(getScrollAmount()));
            button.renderButton(context, mouseX, mouseY, delta);
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

    class CursorButtonWidget extends PressableWidget {
        private static final String PREFIX_TEXT_KEY = "minecraft-cursor.options.cursor-type.";
        private static final int TEXTURE_SIZE = 16;
        private static final int PADDING_LEFT = 8;
        private static final int BACKGROUND_COLOR = 0x7F000000; // black 50%
        private static final int TEXT_COLOR = 0xFFFFFFFF; // white
        private static final int TEXT_DISABLED_COLOR = 0xFF555555; // dark gray
        private static final int BORDER_COLOR = 0xFFFFFFFF; // white

        private final Cursor cursor;

        CursorButtonWidget(int x, int y, int width, int height, Cursor cursor) {
            super(x, y, width, height, Text.empty());
            this.cursor = cursor;
        }

        @Override
        public void onPress() {
            optionsScreen.selectCursor(cursor);
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
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
            // unsupported
        }
    }
}
