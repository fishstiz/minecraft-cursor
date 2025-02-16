package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.gui.screen.CursorOptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.RenderLayer;
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
        super(client, width + SCROLLBAR_OFFSET, optionsScreen.layout.getContentHeight(), optionsScreen.layout.getHeaderHeight(), ITEM_HEIGHT + ROW_GAP);
        this.optionsScreen = optionsScreen;
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
    protected int getScrollbarX() {
        return getRight() - 6;
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        refreshScroll();
        for (CursorWidgetEntry entry : entries) {
            entry.button.setX(x);
        }
    }

    @Override
    protected void drawHeaderAndFooterSeparators(DrawContext context) {
    }

    @Override
    public void drawMenuListBackground(DrawContext context) {
    }

    public class CursorWidgetEntry extends Entry<CursorWidgetEntry> {
        public PressableWidget button;

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
        private static final int CURSOR_SIZE = 32;
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
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            renderBox(context);
            renderTexture(context);
            renderMessage(context);

            context.drawBorder(getX(), getY(), getWidth(), getHeight(),
                    isSelected() || cursor == optionsScreen.getSelectedCursor() ? BORDER_COLOR : 0xFF000000);
        }

        private void renderBox(DrawContext context) {
            int x2 = getX() + getWidth();
            int y2 = getY() + getHeight();
            context.fill(getX(), getY(), x2, y2, BACKGROUND_COLOR);
        }

        private void renderTexture(DrawContext context) {
            int x = getX() + PADDING_LEFT;
            int y = getY() + (getHeight() / 2) - (TEXTURE_SIZE / 2);
            int textureHeight = CURSOR_SIZE;
            int frameIndex = 0;

            if (cursor instanceof AnimatedCursor animatedCursor) {
                textureHeight *= animatedCursor.getAvailableFrames();
                frameIndex = optionsScreen.animationHelper.getCurrentSpriteIndex(animatedCursor);
            }

            int vOffset = CURSOR_SIZE * frameIndex;

            context.drawTexture(
                    RenderLayer::getGuiTextured,
                    cursor.getSprite(),
                    x, y,
                    0, vOffset, // starting point
                    TEXTURE_SIZE, TEXTURE_SIZE, // width/height to stretch/shrink
                    CURSOR_SIZE, CURSOR_SIZE, // cropped width/height from actual image
                    CURSOR_SIZE, textureHeight // actual width/height
            );
        }

        private void renderMessage(DrawContext context) {
            int x = getX() + TEXTURE_SIZE + PADDING_LEFT * 2;
            int y = getY() + (getHeight() / 2) - Math.round(client.textRenderer.fontHeight * 1.5f);
            Text name = Text.translatable(PREFIX_TEXT_KEY + cursor.getType().getKey());
            context.drawText(client.textRenderer, name, x, y + Math.round(getHeight() / 3.0f), cursor.isEnabled() ? TEXT_COLOR : TEXT_DISABLED_COLOR, false);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        }
    }
}
