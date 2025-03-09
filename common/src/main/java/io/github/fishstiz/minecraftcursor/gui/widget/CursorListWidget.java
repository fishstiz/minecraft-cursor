package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.gui.screen.CursorOptionsScreen;
import io.github.fishstiz.minecraftcursor.util.DrawUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CursorListWidget extends ContainerObjectSelectionList<CursorListWidget.CursorEntry> {
    private static final int ITEM_HEIGHT = 32;
    private static final int SCROLLBAR_OFFSET = 6;
    private static final int ROW_GAP = 1;
    private final CursorOptionsScreen optionsScreen;

    public CursorListWidget(Minecraft client, int width, int height, int y, CursorOptionsScreen optionsScreen) {
        super(client, width + SCROLLBAR_OFFSET, height, y, ITEM_HEIGHT + ROW_GAP);
        this.optionsScreen = optionsScreen;
        populateEntries();
    }

    public void populateEntries() {
        for (Cursor cursor : optionsScreen.getCursors()) {
            CursorEntry entry = new CursorEntry(
                    cursor,
                    getX(),
                    getY() + itemHeight * getItemCount(),
                    width - SCROLLBAR_OFFSET,
                    ITEM_HEIGHT
            );
            this.addEntry(entry);
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return getRight() - SCROLLBAR_OFFSET;
    }

    @Override
    protected void renderListSeparators(GuiGraphics context) {
        // override to remove header and footer
    }

    @Override
    public void renderListBackground(GuiGraphics context) {
        // override to remove background
    }

    class CursorEntry extends Entry<CursorEntry> {
        public final CursorButtonWidget button;

        public CursorEntry(Cursor cursor, int x, int y, int width, int height) {
            button = new CursorButtonWidget(x, y, width, height, cursor);
        }

        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
            button.setX(CursorListWidget.this.getX());
            button.setY(CursorListWidget.this.getY() + (itemHeight + ROW_GAP) * index - (int) Math.round(getScrollAmount()));
            button.render(context, mouseX, mouseY, delta);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(button);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(button);
        }
    }

    class CursorButtonWidget extends AbstractButton {
        private static final String PREFIX_TEXT_KEY = "minecraft-cursor.options.cursor-type.";
        private static final int TEXTURE_SIZE = 16;
        private static final int PADDING_LEFT = 8;
        private static final int BACKGROUND_COLOR = 0x7F000000; // black 50%
        private static final int TEXT_COLOR = 0xFFFFFFFF; // white
        private static final int TEXT_DISABLED_COLOR = 0xFF555555; // dark gray
        private static final int BORDER_COLOR = 0xFFFFFFFF; // white

        private final Cursor cursor;

        CursorButtonWidget(int x, int y, int width, int height, Cursor cursor) {
            super(x, y, width, height, Component.empty());
            this.cursor = cursor;
        }

        @Override
        public void onPress() {
            optionsScreen.selectCursor(cursor);
        }

        @Override
        protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
            renderBox(context);
            renderTexture(context);
            renderMessage(context);

            context.renderOutline(getX(), getY(), getWidth(), getHeight(),
                    isHoveredOrFocused() || cursor == optionsScreen.getSelectedCursor() ? BORDER_COLOR : 0xFF000000);
        }

        private void renderBox(GuiGraphics context) {
            int x2 = getX() + getWidth();
            int y2 = getY() + getHeight();
            context.fill(getX(), getY(), x2, y2, BACKGROUND_COLOR);
        }

        private void renderTexture(GuiGraphics context) {
            int x = getX() + PADDING_LEFT;
            int y = getY() + (getHeight() / 2) - (TEXTURE_SIZE / 2);
            optionsScreen.animationHelper.drawSprite(context, cursor, x, y, TEXTURE_SIZE);
        }

        private void renderMessage(GuiGraphics context) {
            Component message = Component.translatable(PREFIX_TEXT_KEY + this.cursor.getType().getKey());
            int color = cursor.isEnabled() ? TEXT_COLOR : TEXT_DISABLED_COLOR;
            int x = getX() + TEXTURE_SIZE + PADDING_LEFT * 2;
            int endX = (getX() + getWidth()) - SCROLLBAR_OFFSET;
            int endY = getY() + getHeight();

            DrawUtil.drawScrollableTextLeftAlign(context, minecraft.font, message, x, getY(), endX, endY, color);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput builder) {
            // unsupported
        }
    }
}
