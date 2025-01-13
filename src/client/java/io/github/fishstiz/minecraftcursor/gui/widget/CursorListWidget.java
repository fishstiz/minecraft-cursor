package io.github.fishstiz.minecraftcursor.gui.widget;

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
    private final List<CursorWidgetEntry> entries = new ArrayList<>();
    private final CursorOptionsScreen optionsScreen;

    public CursorListWidget(MinecraftClient client, int width, CursorOptionsScreen optionsScreen) {
        super(client, width, optionsScreen.layout.getContentHeight(), optionsScreen.layout.getHeaderHeight(), ITEM_HEIGHT);
        this.optionsScreen = optionsScreen;
        populateEntries();
    }

    public void populateEntries() {
        List<Cursor> cursors = optionsScreen.getCursors();
        for (int i = 0; i < cursors.size(); i++) {
            int y = this.getY() * (i + 1);
            CursorWidgetEntry entry = new CursorWidgetEntry(cursors.get(i), y, width, itemHeight, client, optionsScreen);
            entries.add(entry);
            this.addEntry(entry);

            if (i == 0) {
                entry.setFocused(true);
            }
        }
    }

    @Override
    public void setX(int x) {
        super.setX(x);
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

    public static class CursorWidgetEntry extends ElementListWidget.Entry<CursorListWidget.CursorWidgetEntry> {
        public PressableWidget button;

        public CursorWidgetEntry(Cursor cursor, int y, int width, int height, MinecraftClient client, CursorOptionsScreen optionsScreen) {
            button = new CursorClickableWidget(0, y, width, height, cursor, client, optionsScreen);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
            button.render(context, mouseX, mouseY, delta);
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

            context.drawBorder(getX(), getY(), getWidth(), getHeight(), isSelected() ? BORDER_COLOR : 0xFF000000);
        }

        private void renderBox(DrawContext context) {
            int x2 = getX() + getWidth();
            int y2 = getY() + getHeight();
            context.fill(getX(), getY(), x2, y2, BACKGROUND_COLOR);
        }

        private void renderTexture(DrawContext context) {
            int x = getX() + PADDING_LEFT;
            int y = getY() + (getHeight() / 2) - (TEXTURE_SIZE / 2);
            context.drawTexture(RenderLayer::getGuiTextured, cursor.getSprite(), x, y, 0, 0, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);
        }

        private void renderMessage(DrawContext context) {
            int x = getX() + TEXTURE_SIZE + PADDING_LEFT * 2;
            int y = getY() + (getHeight() / 2) - Math.round(client.textRenderer.fontHeight * 1.5f);
            Text name = Text.translatable(PREFIX_TEXT_KEY + cursor.getType().getKey());
            context.drawText(client.textRenderer, name, x, y + Math.round(getHeight() / 3.0f), TEXT_COLOR, false);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        }
    }
}
