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

import static io.github.fishstiz.minecraftcursor.registry.gui.ingame.HandledScreenCursor.screenHandler;
import static io.github.fishstiz.minecraftcursor.registry.gui.ingame.HandledScreenCursor.focusedSlot;

public class CrafterScreenCursor {
    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        if (screenHandler == null || focusedSlot == null) {
            MinecraftCursor.LOGGER.warn("Cannot register cursor type for CrafterScreen");
            return;
        }
        cursorTypeRegistry.register(CrafterScreen.class, CrafterScreenCursor::getCrafterCursorType);
    }

    public static CursorType getCrafterCursorType(Element element, double mouseX, double mouseY) {
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
