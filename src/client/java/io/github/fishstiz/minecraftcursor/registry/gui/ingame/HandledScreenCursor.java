package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.registry.utils.LookupUtils;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

import java.lang.invoke.VarHandle;

public class HandledScreenCursor {
    protected static final String HANDLER_NAME = "field_2797";
    protected static VarHandle screenHandler;

    public static void register() {
        try {
            initHandles();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            MinecraftCursor.LOGGER.warn("Could not register cursor type for HandledScreen");
        }
    }

    public static void initHandles() throws NoSuchFieldException, IllegalAccessException {
        screenHandler = LookupUtils.getVarHandle(HandledScreen.class, HANDLER_NAME, ScreenHandler.class);
    }
}
