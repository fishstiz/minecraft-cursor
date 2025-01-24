package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.client.access.HandledScreenAccessor;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.utils.CursorTypeUtils;
import io.github.fishstiz.minecraftcursor.registry.gui.GuiCursorHandler;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class HandledScreenCursor<T extends ScreenHandler> extends GuiCursorHandler {
    protected HandledScreenCursor() {
    }

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        cursorTypeRegistry.register(HandledScreen.class, new HandledScreenCursor<>()::getCursorType);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected CursorType getCursorType(Element element, double mouseX, double mouseY) {
        CursorConfig config = MinecraftCursorClient.CONFIG.get();

        HandledScreenAccessor<T> handledScreen = (HandledScreenAccessor<T>) element;
        ScreenHandler handler = handledScreen.getHandler();
        Slot focusedSlot = handledScreen.getFocusedSlot();

        boolean canClickFocusedSlot = handler.getCursorStack().isEmpty()
                && focusedSlot != null
                && focusedSlot.hasStack()
                && focusedSlot.canBeHighlighted();

        if (canClickFocusedSlot && CursorTypeUtils.canShift()) {
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
