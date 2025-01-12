package io.github.fishstiz.minecraftcursor.registry;

import io.github.fishstiz.minecraftcursor.registry.widget.CursorPointerWidgetRegistry;
import io.github.fishstiz.minecraftcursor.registry.widget.CursorTextWidgetRegistry;
import io.github.fishstiz.minecraftcursor.registry.widget.EntryListWidgetCursorRegistry;
import io.github.fishstiz.minecraftcursor.registry.widget.WorldListWidgetCursorRegistry;
import net.minecraft.client.gui.Element;

public class WidgetCursorRegistry extends CursorTypeRegistry {
    private final CursorPointerWidgetRegistry pointerRegistry;
    private final CursorTextWidgetRegistry textRegistry;

    public WidgetCursorRegistry() {
        this.pointerRegistry = new CursorPointerWidgetRegistry(this);
        this.textRegistry = new CursorTextWidgetRegistry(this);
        new EntryListWidgetCursorRegistry(this);
        new WorldListWidgetCursorRegistry(this);
    }

    public void registerPointer(String fullyQualifiedClassName) {
        pointerRegistry.register(fullyQualifiedClassName);
    }

    public void registerPointer(Class<? extends Element> widgetClass) {
        pointerRegistry.register(widgetClass);
    }

    public void registerText(String fullyQualifiedClassName) {
        textRegistry.register(fullyQualifiedClassName);
    }

    public void registerText(Class<? extends Element> widgetClass) {
        textRegistry.register(widgetClass);
    }
}
