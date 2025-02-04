package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.mixin.client.access.HandledScreenAccessor;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;


public class HandledScreenCursorHandler<T extends ScreenHandler, U extends HandledScreen<? extends T>> implements CursorHandler<U> {
    @Override
    @SuppressWarnings("unchecked")
    public CursorType getCursorType(U handledScreen, double mouseX, double mouseY) {
        CursorConfig config = MinecraftCursorClient.CONFIG.get();

        ScreenHandler handler = ((HandledScreenAccessor<T>) handledScreen).getHandler();
        Slot focusedSlot = ((HandledScreenAccessor<T>) handledScreen).getFocusedSlot();

        boolean canClickFocusedSlot = handler.getCursorStack().isEmpty()
                && focusedSlot != null
                && focusedSlot.hasStack()
                && focusedSlot.canBeHighlighted();

        if (canClickFocusedSlot && CursorTypeUtil.canShift()) {
            return CursorType.SHIFT;
        }
        if (config.isItemSlotEnabled() && canClickFocusedSlot) {
            return CursorType.POINTER;
        }
        if (config.isItemGrabbingEnabled() && !handler.getCursorStack().isEmpty()) {
            return CursorType.GRABBING;
        }
        return CursorType.DEFAULT;
    }
}
