package io.github.fishstiz.minecraftcursor.registry.elements;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.EntryListWidget;

public class EntryListWidgetCursorRegistry {
    public static CursorType getCursorTypeFromEntryListWidgetEntry(Element entryListWidget, double mouseX, double mouseY, float delta) {
        for (Element entry : ((EntryListWidget<?>) entryListWidget).children()) {
            if (entry.isMouseOver(mouseX, mouseY)) {
                return CursorTypeRegistry.getCursorType(entry, mouseX, mouseY, delta);
            }
        }
        return CursorType.DEFAULT;
    }
    
    public static void init() {
        CursorTypeRegistry.register(EntryListWidget.class, EntryListWidgetCursorRegistry::getCursorTypeFromEntryListWidgetEntry);
    }
}
