package io.github.fishstiz.minecraftcursor.cursorhandler.world;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.gui.screen.world.WorldListWidget;

public class WorldListWidgetCursorHandler implements CursorHandler<WorldListWidget> {
    public static final int ICON_SIZE = 32;

    @Override
    public CursorType getCursorType(WorldListWidget worldListWidget, double mouseX, double mouseY) {
        if (!MinecraftCursor.CONFIG.isWorldIconEnabled()) return CursorType.DEFAULT;

        int x = worldListWidget.getRowLeft();
        for (WorldListWidget.Entry entry : worldListWidget.children()) {
            if (entry instanceof WorldListWidget.WorldEntry worldEntry
                    && entry.isMouseOver(mouseX, mouseY)
                    && mouseX >= x - 1
                    && mouseX <= x - 1 + ICON_SIZE
                    && worldEntry.isAvailable()) {
                return CursorType.POINTER;
            }
        }
        return CursorType.DEFAULT;
    }
}
