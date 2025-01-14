package io.github.fishstiz.minecraftcursor.registry.widget;

import io.github.fishstiz.minecraftcursor.registry.WidgetCursorRegistry;

public class ModMenuCursorRegistry {
    public ModMenuCursorRegistry(WidgetCursorRegistry widgetCursorRegistry) {
        widgetCursorRegistry.registerPointer("com.terraformersmc.modmenu.gui.widget.DescriptionListWidget$MojangCreditsEntry");
        widgetCursorRegistry.registerPointer("com.terraformersmc.modmenu.gui.widget.DescriptionListWidget$LinkEntry");
    }
}
