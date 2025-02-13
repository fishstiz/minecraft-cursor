package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.cursorhandler.AbstractHandledScreenCursorHandler;
import io.github.fishstiz.minecraftcursor.impl.HandleScreenCursorProvider;
import io.github.fishstiz.minecraftcursor.mixin.client.access.HandledScreenAccessor;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;

public class HandledScreenCursorHandler<T extends ScreenHandler, U extends HandledScreen<? extends T>> extends AbstractHandledScreenCursorHandler<T, U> {
    @Override
    public CursorType getCursorType(U handledScreen, double mouseX, double mouseY) {
        CursorType cursorType = computeCursorType(handledScreen);
        HandleScreenCursorProvider.setCursorType(cursorType);
        return cursorType;
    }

    private CursorType computeCursorType(U handledScreen) {
        ScreenHandler handler = ((HandledScreenAccessor<?>) handledScreen).getHandler();
        Slot focusedSlot = ((HandledScreenAccessor<?>) handledScreen).getFocusedSlot();

        boolean canClickFocusedSlot = handler.getCursorStack().isEmpty()
                && focusedSlot != null
                && focusedSlot.hasStack()
                && focusedSlot.canBeHighlighted();

        if (canClickFocusedSlot && CursorTypeUtil.canShift()) {
            return CursorType.SHIFT;
        }
        if (CONFIG.isItemSlotEnabled() && canClickFocusedSlot) {
            return CursorType.POINTER;
        }
        if (CONFIG.isItemGrabbingEnabled() && !handler.getCursorStack().isEmpty()) {
            return CursorType.GRABBING;
        }
        return CursorType.DEFAULT;
    }
}
