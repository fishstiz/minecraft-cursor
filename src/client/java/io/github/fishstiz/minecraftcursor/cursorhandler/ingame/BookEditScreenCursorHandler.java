package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.client.access.BookEditScreenAccessor;

import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;

public class BookEditScreenCursorHandler implements CursorHandler<BookEditScreen> {
    // Manually set values from testing in game
    public static final int MAX_POS_X = 115;
    public static final int MAX_POS_Y = 125;

    @Override
    public CursorType getCursorType(BookEditScreen element, double mouseX, double mouseY) {
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
