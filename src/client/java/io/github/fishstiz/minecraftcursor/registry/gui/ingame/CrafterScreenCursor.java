package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.client.access.HandledScreenAccessor;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.CrafterScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.CrafterScreenHandler;
import net.minecraft.screen.slot.CrafterInputSlot;
import net.minecraft.screen.slot.Slot;

public class CrafterScreenCursor extends HandledScreenCursor<CrafterScreenHandler> {
    private CrafterScreenCursor() {
    }

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        cursorTypeRegistry.register(CrafterScreen.class, new CrafterScreenCursor()::getCursorType);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected CursorType getCursorType(Element element, double mouseX, double mouseY) {
        CursorType cursorType = super.getCursorType(element, mouseX, mouseY);
        if (cursorType != CursorType.DEFAULT) return cursorType;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return CursorType.DEFAULT;

        HandledScreenAccessor<CrafterScreenHandler> crafterScreen = (HandledScreenAccessor<CrafterScreenHandler>) element;
        Slot focusedSlot = crafterScreen.getFocusedSlot();
        if (focusedSlot instanceof CrafterInputSlot
                && crafterScreen.getHandler().getCursorStack().isEmpty()
                && !focusedSlot.hasStack()
                && !player.isSpectator()) {
            return CursorType.POINTER;
        }
        return CursorType.DEFAULT;
    }
}
