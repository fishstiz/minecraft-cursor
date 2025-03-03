package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.cursorhandler.AbstractHandledScreenCursorHandler;
import io.github.fishstiz.minecraftcursor.provider.HandledScreenCursorProvider;
import io.github.fishstiz.minecraftcursor.mixin.access.HandledScreenAccessor;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;

public class HandledScreenCursorHandler<T extends AbstractContainerMenu, U extends AbstractContainerScreen<? extends T>> extends AbstractHandledScreenCursorHandler<T, U> {
    @Override
    public CursorType getCursorType(U handledScreen, double mouseX, double mouseY) {
        CursorType cursorType = computeCursorType(handledScreen);
        HandledScreenCursorProvider.setCursorType(cursorType);
        return cursorType;
    }

    private CursorType computeCursorType(U handledScreen) {
        AbstractContainerMenu handler = ((HandledScreenAccessor<?>) handledScreen).getHandler();
        Slot focusedSlot = ((HandledScreenAccessor<?>) handledScreen).getFocusedSlot();

        boolean canClickFocusedSlot = handler.getCarried().isEmpty()
                && focusedSlot != null
                && focusedSlot.hasItem()
                && focusedSlot.isHighlightable();

        if (canClickFocusedSlot && CursorTypeUtil.canShift()) {
            return CursorType.SHIFT;
        }
        if (CONFIG.isItemSlotEnabled() && canClickFocusedSlot) {
            return CursorType.POINTER;
        }
        if (CONFIG.isItemGrabbingEnabled() && !handler.getCarried().isEmpty()) {
            return CursorType.GRABBING;
        }
        return CursorType.DEFAULT;
    }
}
