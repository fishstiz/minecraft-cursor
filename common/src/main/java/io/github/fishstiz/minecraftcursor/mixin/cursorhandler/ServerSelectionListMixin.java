package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.cursor.handler.InternalCursorProvider;
import io.github.fishstiz.minecraftcursor.mixin.cursorhandler.access.OnlineServerEntryAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerSelectionList.class)
public abstract class ServerSelectionListMixin extends ObjectSelectionList<ServerSelectionList.Entry> implements InternalCursorProvider {
    @Unique
    private static final int minecraft_cursor$ICON_WIDTH = 32;

    @Unique
    private static final int minecraft_cursor$MOVE_ICON_SIZE = 16;

    protected ServerSelectionListMixin(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
        super(minecraft, width, height, y0, y1, itemHeight);
    }

    @Override
    public @NotNull CursorType minecraft_cursor$getCursorType(double mouseX, double mouseY) {
        if (MinecraftCursor.CONFIG.isServerIconEnabled()
            && mouseX < this.getRowLeft() + minecraft_cursor$ICON_WIDTH
            && this.getEntryAtPosition(mouseX, mouseY) instanceof ServerSelectionList.OnlineServerEntry serverEntry) {
            OnlineServerEntryAccessor accessor = (OnlineServerEntryAccessor) serverEntry;
            int i = this.children().indexOf(serverEntry);
            double relativeX = mouseX - this.getRowLeft();
            double relativeY = mouseY - this.getRowTop(i);

            if (relativeX < minecraft_cursor$ICON_WIDTH && relativeX > minecraft_cursor$MOVE_ICON_SIZE && accessor.invokeCanJoin()) {
                return CursorType.POINTER;
            }
            if (relativeX < minecraft_cursor$MOVE_ICON_SIZE && relativeY < minecraft_cursor$MOVE_ICON_SIZE && i > 0) {
                return CursorType.POINTER;
            }
            if (relativeX < minecraft_cursor$MOVE_ICON_SIZE && relativeY > minecraft_cursor$MOVE_ICON_SIZE && i < accessor.getScreen().getServers().size() - 1) {
                return CursorType.POINTER;
            }
        }

        return CursorType.DEFAULT;
    }
}
