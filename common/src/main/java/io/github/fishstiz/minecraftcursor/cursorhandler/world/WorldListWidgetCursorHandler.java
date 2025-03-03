package io.github.fishstiz.minecraftcursor.cursorhandler.world;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;

public class WorldListWidgetCursorHandler implements CursorHandler<WorldSelectionList> {
    @Override
    public CursorType getCursorType(WorldSelectionList worldListWidget, double mouseX, double mouseY) {
        if (!MinecraftCursor.CONFIG.isWorldIconEnabled()) return CursorType.DEFAULT;

        int x = (int) Math.floor((double) worldListWidget.getWidth() / 2 - (double) worldListWidget.getRowWidth() / 2);
        for (WorldSelectionList.Entry entry : worldListWidget.children()) {
            if (entry.isMouseOver(mouseX, mouseY) && mouseX >= x && mouseX <= x + 32 && ((WorldSelectionList.WorldListEntry) entry).canJoin()) {
                return CursorType.POINTER;
            }
        }
        return CursorType.DEFAULT;
    }
}
