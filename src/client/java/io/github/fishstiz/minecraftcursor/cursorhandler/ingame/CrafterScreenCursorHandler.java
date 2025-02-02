package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.client.access.HandledScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CrafterScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.CrafterScreenHandler;
import net.minecraft.screen.slot.CrafterInputSlot;
import net.minecraft.screen.slot.Slot;

public class CrafterScreenCursorHandler extends HandledScreenCursorHandler<CrafterScreenHandler, CrafterScreen> {
    @Override
    @SuppressWarnings("unchecked")
    public CursorType getCursorType(CrafterScreen crafterScreen, double mouseX, double mouseY) {
        CursorType cursorType = super.getCursorType(crafterScreen, mouseX, mouseY);
        if (cursorType != CursorType.DEFAULT) return cursorType;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return CursorType.DEFAULT;

        Slot focusedSlot = ((HandledScreenAccessor<CrafterScreenHandler>) crafterScreen).getFocusedSlot();
        if (focusedSlot instanceof CrafterInputSlot
                && ((HandledScreenAccessor<CrafterScreenHandler>) crafterScreen).getHandler().getCursorStack().isEmpty()
                && !focusedSlot.hasStack()
                && !player.isSpectator()) {
            return CursorType.POINTER;
        }
        return CursorType.DEFAULT;
    }
}
