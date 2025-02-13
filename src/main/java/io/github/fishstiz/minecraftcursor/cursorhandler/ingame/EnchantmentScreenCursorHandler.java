package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.client.access.HandledScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.EnchantmentScreenHandler;

public class EnchantmentScreenCursorHandler extends HandledScreenCursorHandler<EnchantmentScreenHandler, EnchantmentScreen> {
    // Derived from EnchantmentScreen#drawBackground
    public static final int ENCHANTMENT_BTN_WIDTH = 108;
    public static final int ENCHANTMENT_BTN_HEIGHT = 19;
    public static final int ENCHANTMENT_BTN_OFFSET_X = 60;
    public static final int ENCHANTMENT_BTN_OFFSET_Y = 14;

    @Override
    @SuppressWarnings("unchecked")
    public CursorType getCursorType(EnchantmentScreen enchantmentScreen, double mouseX, double mouseY) {
        CursorType cursorType = super.getCursorType(enchantmentScreen, mouseX, mouseY);
        if (cursorType != CursorType.DEFAULT) return cursorType;

        if (!MinecraftCursor.CONFIG.isEnchantmentsEnabled()) return CursorType.DEFAULT;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return CursorType.DEFAULT;

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
