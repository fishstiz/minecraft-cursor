package io.github.fishstiz.minecraftcursor.cursorhandler.multiplayer;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;

public class MultiplayerServerListWidgetCursorHandler implements CursorHandler<ServerSelectionList> {
    public static final int ICON_SIZE = 32;

    @Override
    public CursorType getCursorType(ServerSelectionList serverList, double mouseX, double mouseY) {
        if (!MinecraftCursor.CONFIG.isServerIconEnabled()) return CursorType.DEFAULT;

        ServerSelectionList.OnlineServerEntry serverEntry =
                (ServerSelectionList.OnlineServerEntry) serverList.getChildAt(mouseX, mouseY)
                        .filter(entry -> entry instanceof ServerSelectionList.OnlineServerEntry)
                        .orElse(null);
        if (serverEntry != null && mouseX >= serverList.getRowLeft() && mouseX <= serverList.getRowLeft() + ICON_SIZE) {
            return CursorType.POINTER;
        }
        return CursorType.DEFAULT;
    }
}
