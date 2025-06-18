package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.cursor.handler.InternalCursorProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WorldSelectionList.class)
public abstract class WorldSelectionListMixin extends ObjectSelectionList<WorldSelectionList.Entry> implements InternalCursorProvider {
    @Unique
    private static final int minecraft_cursor$ICON_WIDTH = 32;

    protected WorldSelectionListMixin(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
    }

    @Override
    public @NotNull CursorType minecraft_cursor$getCursorType(double mouseX, double mouseY) {
        if (MinecraftCursor.CONFIG.isWorldIconEnabled()
            && mouseX < this.getRowLeft() + minecraft_cursor$ICON_WIDTH
            && this.getEntryAtPosition(mouseX, mouseY) instanceof WorldSelectionList.WorldListEntry worldEntry
            && worldEntry.canJoin()
        ) {
            return CursorType.POINTER;
        }
        return CursorType.DEFAULT;
    }
}
