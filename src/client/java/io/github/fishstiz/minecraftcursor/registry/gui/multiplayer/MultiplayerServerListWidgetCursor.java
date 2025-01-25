package io.github.fishstiz.minecraftcursor.registry.gui.multiplayer;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.registry.gui.GuiCursorHandler;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;

public class MultiplayerServerListWidgetCursor extends GuiCursorHandler {
    public static final int ICON_SIZE = 32;

    private MultiplayerServerListWidgetCursor() {
    }

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        cursorTypeRegistry.register(MultiplayerServerListWidget.class, new MultiplayerServerListWidgetCursor()::getCursorType);
    }

    @Override
    protected CursorType getCursorType(Element element, double mouseX, double mouseY) {
        MultiplayerServerListWidget serverList = (MultiplayerServerListWidget) element;
        MultiplayerServerListWidget.ServerEntry serverEntry =
                (MultiplayerServerListWidget.ServerEntry) serverList.hoveredElement(mouseX, mouseY)
                        .filter(entry -> entry instanceof MultiplayerServerListWidget.ServerEntry)
                        .orElse(null);
        if (serverEntry != null && mouseX >= serverList.getRowLeft() && mouseX <= serverList.getRowLeft() + ICON_SIZE) {
            return CursorType.POINTER;
        }
        return CursorType.DEFAULT;
    }
}
