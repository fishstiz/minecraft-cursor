package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.registry.utils.LookupUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandler;

import java.lang.invoke.VarHandle;

public class EnchantmentScreenCursor {
    // Derived from EnchantmentScreen.drawBackground()
    public static final int ENCHANTMENT_BTN_WIDTH = 108;
    public static final int ENCHANTMENT_BTN_HEIGHT = 19;
    public static final int ENCHANTMENT_BTN_OFFSET_X = 60;
    public static final int ENCHANTMENT_BTN_OFFSET_Y = 14;

    // EnchantmentScreenHandler
    private static final String BACKGROUND_WIDTH_NAME = "field_2792";
    private static VarHandle backgroundWidth;
    private static final String BACKGROUND_HEIGHT_NAME = "field_2779";
    private static VarHandle backgroundHeight;
    private static final String HANDLER_NAME = "field_2797";
    private static VarHandle screenHandler; // EnchantmentScreenHandler

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        try {
            initHandles();
            cursorTypeRegistry.register(EnchantmentScreen.class, EnchantmentScreenCursor::getCursorType);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            MinecraftCursor.LOGGER.warn("Could not register cursor type for EnchantmentScreen");
        }
    }

    private static void initHandles() throws NoSuchFieldException, IllegalAccessException {
        Class<?> targetClass = HandledScreen.class;
        backgroundWidth = LookupUtils.getVarHandle(targetClass, BACKGROUND_WIDTH_NAME, int.class);
        backgroundHeight = LookupUtils.getVarHandle(targetClass, BACKGROUND_HEIGHT_NAME, int.class);
        screenHandler = LookupUtils.getVarHandle(targetClass, HANDLER_NAME, ScreenHandler.class);
    }

    private static CursorType getCursorType(Element element, double mouseX, double mouseY) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return CursorType.DEFAULT;
        }

        EnchantmentScreen enchantmentScreen = (EnchantmentScreen) element;
        EnchantmentScreenHandler handler =
                (EnchantmentScreenHandler) EnchantmentScreenCursor.screenHandler.get(enchantmentScreen);

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
