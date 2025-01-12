package io.github.fishstiz.minecraftcursor.registry.widget;

import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.registry.WidgetCursorRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class CursorTextWidgetRegistry {
    private final WidgetCursorRegistry cursorTypeRegistry;

    public CursorTextWidgetRegistry(WidgetCursorRegistry cursorTypeRegistry) {
        this.cursorTypeRegistry = cursorTypeRegistry;

        register(TextFieldWidget.class);
        register(ChatScreen.class);
    }

    public void register(String fullyQualifiedClassName) {
        cursorTypeRegistry.register(fullyQualifiedClassName, CursorTypeRegistry::elementToText);
    }

    public void register(Class<? extends Element> widget) {
        cursorTypeRegistry.register(widget, CursorTypeRegistry::elementToText);
    }
}
