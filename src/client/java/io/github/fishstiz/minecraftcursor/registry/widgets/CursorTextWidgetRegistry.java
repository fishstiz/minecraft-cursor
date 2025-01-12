package io.github.fishstiz.minecraftcursor.registry.widgets;

import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class CursorTextWidgetRegistry {
    public static void register(String fullyQualifiedClassName) {
        CursorTypeRegistry.register(fullyQualifiedClassName, CursorTypeRegistry::elementToText);
    }

    public static void register(Class<? extends Element> widget) {
        CursorTypeRegistry.register(widget, CursorTypeRegistry::elementToText);
    }

    public static void init() {
        register(TextFieldWidget.class);
        register(ChatScreen.class);
    }
}
