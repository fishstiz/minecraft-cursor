package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.CrafterScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.CrafterScreenHandler;
import net.minecraft.screen.slot.CrafterInputSlot;
import net.minecraft.screen.slot.Slot;

public class CrafterScreenCursor extends HandledScreenCursor {
    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        if (screenHandler == null || focusedSlot == null) {
            MinecraftCursor.LOGGER.warn("Cannot register cursor type for CrafterScreen");
            return;
        }
        cursorTypeRegistry.register(CrafterScreen.class, CrafterScreenCursor::getCursorType);
    }

    public static CursorType getCursorType(Element element, double mouseX, double mouseY) {
        CursorType handledScreenCursor = HandledScreenCursor.getCursorType(element, mouseX, mouseY);
        if (handledScreenCursor != CursorType.DEFAULT) {
            return handledScreenCursor;
        }

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return CursorType.DEFAULT;
        }

        CrafterScreen crafterScreen = (CrafterScreen) element;
        CrafterScreenHandler handler = (CrafterScreenHandler) screenHandler.get(crafterScreen);
        Slot focusedSlot = (Slot) HandledScreenCursor.focusedSlot.get(crafterScreen);
        if (focusedSlot instanceof CrafterInputSlot
                && handler.getCursorStack().isEmpty()
                && !focusedSlot.hasStack()
                && !player.isSpectator()) {
            return CursorType.POINTER;
        }

        return CursorType.DEFAULT;
    }
}
