package io.github.fishstiz.minecraftcursor.cursorhandler.world;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.gui.screen.world.WorldListWidget;

public class WorldListWidgetCursorHandler implements CursorHandler<WorldListWidget> {
    @Override
    public CursorType getCursorType(WorldListWidget worldListWidget, double mouseX, double mouseY) {
        if (!MinecraftCursorClient.CONFIG.isWorldIconEnabled()) return CursorType.DEFAULT;

        int x = (int) Math.floor((double) worldListWidget.getWidth() / 2 - (double) worldListWidget.getRowWidth() / 2);
        for (WorldListWidget.Entry entry : worldListWidget.children()) {
            if (entry.isMouseOver(mouseX, mouseY) && mouseX >= x && mouseX <= x + 32 && ((WorldListWidget.WorldEntry) entry).isLevelSelectable()) {
                return CursorType.POINTER;
            }
        }
        return CursorType.DEFAULT;
    }
}
