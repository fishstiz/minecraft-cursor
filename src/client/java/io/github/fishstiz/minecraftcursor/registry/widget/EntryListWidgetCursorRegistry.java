package io.github.fishstiz.minecraftcursor.registry.widget;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.WidgetCursorRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.EntryListWidget;

public class EntryListWidgetCursorRegistry {
    private final WidgetCursorRegistry widgetCursorRegistry;

    public EntryListWidgetCursorRegistry(WidgetCursorRegistry widgetCursorRegistry) {
        this.widgetCursorRegistry = widgetCursorRegistry;
        widgetCursorRegistry.register(EntryListWidget.class, this::getCursorTypeFromEntryListWidgetEntry);
    }

    private CursorType getCursorTypeFromEntryListWidgetEntry(Element entryListWidget, double mouseX, double mouseY, float delta) {
        for (Element entry : ((EntryListWidget<?>) entryListWidget).children()) {
            if (entry.isMouseOver(mouseX, mouseY)) {
                return widgetCursorRegistry.getCursorType(entry, mouseX, mouseY, delta);
            }
        }
        return CursorType.DEFAULT;
    }
}
