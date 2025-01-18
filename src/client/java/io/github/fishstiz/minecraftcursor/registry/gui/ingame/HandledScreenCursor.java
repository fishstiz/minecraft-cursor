package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.registry.utils.LookupUtils;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.lang.invoke.VarHandle;

public class HandledScreenCursor {
    protected static final String BACKGROUND_WIDTH_NAME = "field_2792";
    protected static VarHandle backgroundWidth;
    protected static final String BACKGROUND_HEIGHT_NAME = "field_2779";
    protected static VarHandle backgroundHeight;
    protected static final String HANDLER_NAME = "field_2797";
    protected static VarHandle screenHandler;
    protected static final String X_NAME = "field_2776";
    protected static VarHandle x;
    protected static final String Y_NAME = "field_2800";
    protected static VarHandle y;
    protected static final String FOCUSED_SLOT_NAME = "field_2787";
    protected static VarHandle focusedSlot;

    public static void register() {
        try {
            initHandles();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            MinecraftCursor.LOGGER.warn("Could not register cursor type for HandledScreen");
        }
    }

    public static void initHandles() throws NoSuchFieldException, IllegalAccessException {
        Class<?> targetClass = HandledScreen.class;
        screenHandler = LookupUtils.getVarHandle(targetClass, HANDLER_NAME, ScreenHandler.class);
        backgroundWidth = LookupUtils.getVarHandle(targetClass, BACKGROUND_WIDTH_NAME, int.class);
        backgroundHeight = LookupUtils.getVarHandle(targetClass, BACKGROUND_HEIGHT_NAME, int.class);
        x = LookupUtils.getVarHandle(targetClass, X_NAME, int.class);
        y = LookupUtils.getVarHandle(targetClass, Y_NAME, int.class);
        focusedSlot = LookupUtils.getVarHandle(targetClass, FOCUSED_SLOT_NAME, Slot.class);
    }
}
