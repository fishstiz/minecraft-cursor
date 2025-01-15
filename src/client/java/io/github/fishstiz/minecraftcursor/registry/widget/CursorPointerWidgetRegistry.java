package io.github.fishstiz.minecraftcursor.registry.widget;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorHotspotWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorSliderWidget;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.registry.WidgetCursorRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.gui.widget.PressableWidget;

public class CursorPointerWidgetRegistry {
    private final WidgetCursorRegistry cursorTypeRegistry;

    public CursorPointerWidgetRegistry(WidgetCursorRegistry cursorTypeRegistry) {
        this.cursorTypeRegistry = cursorTypeRegistry;
        // Minecraft Classes
        cursorTypeRegistry.register(PressableWidget.class, CursorPointerWidgetRegistry::pressableWidgetCursor);
        register(OptionSliderWidget.class);

        // minecraft-cursor Classes
        register(SelectedCursorSliderWidget.class);
        register(SelectedCursorHotspotWidget.class);
    }

    public void register(String fullyQualifiedClassName) {
        cursorTypeRegistry.register(fullyQualifiedClassName, CursorTypeRegistry::elementToPointer);
    }

    public void register(Class<? extends Element> widget) {
        cursorTypeRegistry.register(widget, CursorTypeRegistry::elementToPointer);
    }

    private static CursorType pressableWidgetCursor(Element entry, double mouseX, double mouseY, float delta) {
        PressableWidget button = (PressableWidget) entry;
        return button.isHovered() && button.active ? CursorType.POINTER : CursorType.DEFAULT;
    }
}
