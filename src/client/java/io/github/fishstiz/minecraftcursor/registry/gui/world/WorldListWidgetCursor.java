package io.github.fishstiz.minecraftcursor.registry.gui.world;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.world.WorldListWidget;

public class WorldListWidgetCursor {
    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        cursorTypeRegistry.register(WorldListWidget.class, WorldListWidgetCursor::getCursorTypeFromWorld);
    }

    private static CursorType getCursorTypeFromWorld(Element element, double mouseX, double mouseY) {
        WorldListWidget worldListWidget = (WorldListWidget) element;
        int x = (int) Math.floor((double) worldListWidget.getWidth() / 2 - (double) worldListWidget.getRowWidth() / 2);
        for (WorldListWidget.Entry entry : worldListWidget.children()) {
            if (entry.isMouseOver(mouseX, mouseY) && mouseX >= x && mouseX <= x + 32 && ((WorldListWidget.WorldEntry) entry).isLevelSelectable()) {
                return CursorType.POINTER;
            }
        }
        return CursorType.DEFAULT;
    }
}
