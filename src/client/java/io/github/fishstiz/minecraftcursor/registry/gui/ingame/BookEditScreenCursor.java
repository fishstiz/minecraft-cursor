package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.widget.ButtonWidget;

public class BookEditScreenCursor {
    private static final int WIDTH = 192;
    private static final int MAX_POS_X = 115;
    private static final int MAX_POS_Y = 125;

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        cursorTypeRegistry.register(BookEditScreen.class, BookEditScreenCursor::getBookScreenCursor);
    }

    public static CursorType getBookScreenCursor(Element element, double mouseX, double mouseY) {
        BookEditScreen bookEditScreen = (BookEditScreen) element;
        Position position = screenPositionToAbsolutePosition(bookEditScreen, (int) mouseX, (int) mouseY);

        for (Element child : bookEditScreen.children()) {
            if (child instanceof ButtonWidget btn && btn.visible &&
                    btn.getMessage().getContent().toString().contains("key='book.finalizeButton'")) {
                return CursorType.DEFAULT;
            }
        }

        if (position.y >= 0 && position.y <= MAX_POS_Y && position.x > 0 && position.x <= MAX_POS_X) {
            return CursorType.TEXT;
        }
        return CursorType.DEFAULT;
    }

    public static class Position {
        public int x;
        public int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static Position screenPositionToAbsolutePosition(Screen screen, int mouseX, int mouseY) {
        return new Position(mouseX - (screen.width - WIDTH) / 2 - 36, mouseY - 32);
    }
}