package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.client.HandledScreenAccessor;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.EnchantmentScreenHandler;

public class EnchantmentScreenCursor extends HandledScreenCursor<EnchantmentScreenHandler> {
    // Derived from EnchantmentScreen#drawBackground
    public static final int ENCHANTMENT_BTN_WIDTH = 108;
    public static final int ENCHANTMENT_BTN_HEIGHT = 19;
    public static final int ENCHANTMENT_BTN_OFFSET_X = 60;
    public static final int ENCHANTMENT_BTN_OFFSET_Y = 14;

    private EnchantmentScreenCursor() {
    }

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        cursorTypeRegistry.register(EnchantmentScreen.class, new EnchantmentScreenCursor()::getCursorType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CursorType getCursorType(Element element, double mouseX, double mouseY) {
        CursorType cursorType = super.getCursorType(element, mouseX, mouseY);
        if (cursorType != CursorType.DEFAULT) return cursorType;

        if (!MinecraftCursorClient.CONFIG.get().isEnchantmentsEnabled()) return CursorType.DEFAULT;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return CursorType.DEFAULT;

        EnchantmentScreen enchantmentScreen = (EnchantmentScreen) element;
        HandledScreenAccessor<EnchantmentScreenHandler> accessor =
                (HandledScreenAccessor<EnchantmentScreenHandler>) enchantmentScreen;
        EnchantmentScreenHandler handler = accessor.getHandler();

        for (int i = 0; i < handler.enchantmentPower.length; i++) {
            int enchantmentPower = handler.enchantmentPower[i];

            boolean isButtonEnabled = enchantmentPower != 0 &&
                    ((handler.getLapisCount() >= i + 1 && player.experienceLevel >= enchantmentPower) ||
                            player.getAbilities().creativeMode);
            if (!isButtonEnabled) {
                continue;
            }

            int containerX = (enchantmentScreen.width - accessor.getBackgroundWidth()) / 2;
            int containerY = (enchantmentScreen.height - accessor.getBackgroundHeight()) / 2;
            int buttonX = containerX + ENCHANTMENT_BTN_OFFSET_X;
            int buttonY = containerY + ENCHANTMENT_BTN_OFFSET_Y + ENCHANTMENT_BTN_HEIGHT * i;
            int relativeX = (int) mouseX - buttonX;
            int relativeY = (int) mouseY - buttonY;

            if (relativeX >= 0
                    && relativeY >= 0
                    && relativeX < ENCHANTMENT_BTN_WIDTH
                    && relativeY < ENCHANTMENT_BTN_HEIGHT) {
                return CursorType.POINTER;
            }
        }
        return CursorType.DEFAULT;
    }
}
