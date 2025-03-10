package io.github.fishstiz.minecraftcursor.cursorhandler.world;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;

public class WorldListWidgetCursorHandler implements CursorHandler<WorldSelectionList> {
    public static final int ICON_SIZE = 32;

    @Override
    public CursorType getCursorType(WorldSelectionList worldListWidget, double mouseX, double mouseY) {
        if (!MinecraftCursor.CONFIG.isWorldIconEnabled()) return CursorType.DEFAULT;

        int x = worldListWidget.getRowLeft();
        for (WorldSelectionList.Entry entry : worldListWidget.children()) {
            if (entry instanceof WorldSelectionList.WorldListEntry worldEntry
                    && entry.isMouseOver(mouseX, mouseY)
                    && mouseX >= x - 1
                    && mouseX <= x - 1 + ICON_SIZE
                    && worldEntry.isSelectable()) {
                return CursorType.POINTER;
            }
        }
        return CursorType.DEFAULT;
    }
}
