package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.EnchantmentScreenHandler;

import static io.github.fishstiz.minecraftcursor.registry.gui.ingame.HandledScreenCursor.screenHandler;
import static io.github.fishstiz.minecraftcursor.registry.gui.ingame.HandledScreenCursor.backgroundWidth;
import static io.github.fishstiz.minecraftcursor.registry.gui.ingame.HandledScreenCursor.backgroundHeight;

public class EnchantmentScreenCursor {
    // Derived from EnchantmentScreen.drawBackground()
    public static final int ENCHANTMENT_BTN_WIDTH = 108;
    public static final int ENCHANTMENT_BTN_HEIGHT = 19;
    public static final int ENCHANTMENT_BTN_OFFSET_X = 60;
    public static final int ENCHANTMENT_BTN_OFFSET_Y = 14;

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        if (screenHandler == null || backgroundWidth == null || backgroundHeight == null) {
            MinecraftCursor.LOGGER.warn("Could not register cursor type for EnchantmentScreen");
            return;
        }
        cursorTypeRegistry.register(EnchantmentScreen.class, EnchantmentScreenCursor::getCursorType);
    }

    private static CursorType getCursorType(Element element, double mouseX, double mouseY) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return CursorType.DEFAULT;
        }

        EnchantmentScreen enchantmentScreen = (EnchantmentScreen) element;
        EnchantmentScreenHandler handler =
                (EnchantmentScreenHandler) screenHandler.get(enchantmentScreen);

        for (int i = 0; i < handler.enchantmentPower.length; i++) {
            int enchantmentPower = handler.enchantmentPower[i];

            boolean isButtonEnabled = enchantmentPower != 0 &&
                    ((handler.getLapisCount() >= i + 1 && player.experienceLevel >= enchantmentPower) ||
                            player.getAbilities().creativeMode);
            if (!isButtonEnabled) {
                continue;
            }

            int containerX = (enchantmentScreen.width - (int) backgroundWidth.get(enchantmentScreen)) / 2;
            int containerY = (enchantmentScreen.height - (int) backgroundHeight.get(enchantmentScreen)) / 2;
            int buttonX = containerX + ENCHANTMENT_BTN_OFFSET_X;
            int buttonY = containerY + ENCHANTMENT_BTN_OFFSET_Y + ENCHANTMENT_BTN_HEIGHT * i;
            int relativeX = (int) mouseX - buttonX;
            int relativeY = (int) mouseY - buttonY;

            boolean isPointWithinBounds = relativeX >= 0 && relativeY >= 0 &&
                    relativeX < ENCHANTMENT_BTN_WIDTH && relativeY < ENCHANTMENT_BTN_HEIGHT;

            if (isPointWithinBounds) {
                return CursorType.POINTER;
            }
        }
        return CursorType.DEFAULT;
    }
}
