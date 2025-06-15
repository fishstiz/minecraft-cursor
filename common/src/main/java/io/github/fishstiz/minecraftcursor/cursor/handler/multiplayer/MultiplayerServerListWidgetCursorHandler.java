package io.github.fishstiz.minecraftcursor.cursor.handler.multiplayer;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.cursorhandler.access.OnlineServerEntryAccessor;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;

import java.util.List;

public class MultiplayerServerListWidgetCursorHandler implements CursorHandler<ServerSelectionList> {
    private static final int ICON_SIZE = 32;
    private static final int MOVE_ICON_SIZE = ICON_SIZE / 2;

    @Override
    public CursorType getCursorType(ServerSelectionList serverList, double mouseX, double mouseY) {
        if (!MinecraftCursor.CONFIG.isServerIconEnabled()) return CursorType.DEFAULT;

        if (mouseX <= serverList.getRowLeft() + ICON_SIZE) {
            List<ServerSelectionList.Entry> entries = serverList.children();
            for (int i = 0; i <= entries.size(); i++) {
                if (entries.get(i) instanceof ServerSelectionList.OnlineServerEntry entry && entry.isMouseOver(mouseX, mouseY)) {
                    OnlineServerEntryAccessor accessor = (OnlineServerEntryAccessor) entry;
                    double relativeX = mouseX - serverList.getRowLeft();
                    double relativeY = mouseY - serverList.getRowTop(i);

                    if (relativeX < ICON_SIZE && relativeX > MOVE_ICON_SIZE && accessor.invokeCanJoin()) {
                        return CursorType.POINTER;
                    }
                    if (relativeX < MOVE_ICON_SIZE && relativeY < MOVE_ICON_SIZE && i > 0) {
                        return CursorType.POINTER;
                    }
                    if (relativeX < MOVE_ICON_SIZE && relativeY > MOVE_ICON_SIZE && i < accessor.getScreen().getServers().size() - 1) {
                        return CursorType.POINTER;
                    }
                }
            }
        }

        return CursorType.DEFAULT;
    }
}
