package io.github.fishstiz.minecraftcursor.cursor.handler.world;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;

public class WorldListWidgetCursorHandler implements CursorHandler<WorldSelectionList> {
    private static final int ICON_WIDTH = 32;

    @Override
    public CursorType getCursorType(WorldSelectionList worldListWidget, double mouseX, double mouseY) {
        if (!MinecraftCursor.CONFIG.isWorldIconEnabled()) return CursorType.DEFAULT;

        if (mouseX <= worldListWidget.getRowLeft() + ICON_WIDTH) {
            for (WorldSelectionList.Entry entry : worldListWidget.children()) {
                if (entry instanceof WorldSelectionList.WorldListEntry worldListEntry
                    && worldListEntry.isMouseOver(mouseX, mouseY)
                    && worldListEntry.canJoin()) {
                    return CursorType.POINTER;
                }
            }
        }
        return CursorType.DEFAULT;
    }
}
