package io.github.fishstiz.minecraftcursor.cursorhandler.multiplayer;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;

public class MultiplayerServerListWidgetCursorHandler implements CursorHandler<MultiplayerServerListWidget> {
    public static final int ICON_SIZE = 32;

    @Override
    public CursorType getCursorType(MultiplayerServerListWidget serverList, double mouseX, double mouseY) {
        if (!MinecraftCursorClient.CONFIG.get().isServerIconEnabled()) return CursorType.DEFAULT;

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
