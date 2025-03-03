package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.access.HandledScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CrafterScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CrafterSlot;
import net.minecraft.world.inventory.Slot;

public class CrafterScreenCursorHandler extends HandledScreenCursorHandler<CrafterMenu, CrafterScreen> {
    @Override
    @SuppressWarnings("unchecked")
    public CursorType getCursorType(CrafterScreen crafterScreen, double mouseX, double mouseY) {
        CursorType cursorType = super.getCursorType(crafterScreen, mouseX, mouseY);
        if (cursorType != CursorType.DEFAULT) return cursorType;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return CursorType.DEFAULT;

        Slot focusedSlot = ((HandledScreenAccessor<CrafterMenu>) crafterScreen).getFocusedSlot();
        if (focusedSlot instanceof CrafterSlot
                && ((HandledScreenAccessor<CrafterMenu>) crafterScreen).getHandler().getCarried().isEmpty()
                && !focusedSlot.hasItem()
                && !player.isSpectator()) {
            return CursorType.POINTER;
        }
        return CursorType.DEFAULT;
    }
}
