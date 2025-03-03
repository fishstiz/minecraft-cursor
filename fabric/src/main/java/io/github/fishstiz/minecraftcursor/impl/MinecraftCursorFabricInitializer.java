package io.github.fishstiz.minecraftcursor.impl;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorTypeRegistrar;
import io.github.fishstiz.minecraftcursor.api.ElementRegistrar;
import io.github.fishstiz.minecraftcursor.api.MinecraftCursorInitializer;
import io.github.fishstiz.minecraftcursor.cursorhandler.modmenu.ModScreenCursorHandler;
import net.fabricmc.loader.api.FabricLoader;

public class MinecraftCursorFabricInitializer implements MinecraftCursorInitializer {
    @Override
    public void init(CursorTypeRegistrar cursorRegistrar, ElementRegistrar elementRegistrar) {
        try {
            if (FabricLoader.getInstance().isModLoaded("modmenu")) {
                elementRegistrar.register(new ModScreenCursorHandler());
                elementRegistrar.register("com.terraformersmc.modmenu.gui.widget.DescriptionListWidget$MojangCreditsEntry", ElementRegistrar::elementToPointer);
                elementRegistrar.register("com.terraformersmc.modmenu.gui.widget.DescriptionListWidget$LinkEntry", ElementRegistrar::elementToPointer);
            }
        } catch (LinkageError | Exception e) {
            MinecraftCursor.LOGGER.warn("Could not register cursor type for Mod Menu");
        }
    }
}
