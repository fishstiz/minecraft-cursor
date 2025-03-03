package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.access.BookEditScreenAccessor;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;

public class BookEditScreenCursorHandler implements CursorHandler<BookEditScreen> {
    // Manually set values from testing in game
    public static final int MAX_POS_X = 115;
    public static final int MAX_POS_Y = 125;

    @Override
    public CursorType getCursorType(BookEditScreen element, double mouseX, double mouseY) {
        if (!MinecraftCursor.CONFIG.isBookEditEnabled()) return CursorType.DEFAULT;

        BookEditScreenAccessor bookEditScreen = (BookEditScreenAccessor) element;

        if (bookEditScreen.getFinalizeButton().visible) return CursorType.DEFAULT;

        BookEditScreen.Pos2i position =
                bookEditScreen.invokeScreenPosToAbsPos(new BookEditScreen.Pos2i((int) mouseX, (int) mouseY));
        if (position.y >= 0 && position.y <= MAX_POS_Y && position.x > 0 && position.x <= MAX_POS_X) {
            return CursorType.TEXT;
        }
        return CursorType.DEFAULT;
    }
}
