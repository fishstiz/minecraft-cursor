package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.client.BookEditScreenAccessor;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;

import io.github.fishstiz.minecraftcursor.registry.gui.GuiCursorHandler;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;

public class BookEditScreenCursor extends GuiCursorHandler {
    // Manually set values from testing in game
    public static final int MAX_POS_X = 115;
    public static final int MAX_POS_Y = 125;

    private BookEditScreenCursor() {
    }

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        cursorTypeRegistry.register(BookEditScreen.class, new BookEditScreenCursor()::getCursorType);
    }

    @Override
    protected CursorType getCursorType(Element element, double mouseX, double mouseY) {
        if (!MinecraftCursorClient.CONFIG.get().isBookEditEnabled()) return CursorType.DEFAULT;

        BookEditScreenAccessor bookEditScreen = (BookEditScreenAccessor) element;

        if (bookEditScreen.getFinalizeButton().visible) return CursorType.DEFAULT;

        BookEditScreen.Position position =
                bookEditScreen.invokeScreenPosToAbsPos(new BookEditScreen.Position((int) mouseX, (int) mouseY));
        if (position.y >= 0 && position.y <= MAX_POS_Y && position.x > 0 && position.x <= MAX_POS_X) {
            return CursorType.TEXT;
        }
        return CursorType.DEFAULT;
    }
}
