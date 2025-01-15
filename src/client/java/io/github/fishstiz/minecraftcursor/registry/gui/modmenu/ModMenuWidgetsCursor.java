package io.github.fishstiz.minecraftcursor.registry.gui.modmenu;

import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;

public class ModMenuWidgetsCursor {
    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        cursorTypeRegistry.register("com.terraformersmc.modmenu.gui.widget.DescriptionListWidget$MojangCreditsEntry", CursorTypeRegistry::elementToPointer);
        cursorTypeRegistry.register("com.terraformersmc.modmenu.gui.widget.DescriptionListWidget$LinkEntry", CursorTypeRegistry::elementToPointer);
    }
}
