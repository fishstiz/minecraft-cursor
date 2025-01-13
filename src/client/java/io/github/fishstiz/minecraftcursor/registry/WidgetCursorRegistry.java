package io.github.fishstiz.minecraftcursor.registry;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.widget.CursorPointerWidgetRegistry;
import io.github.fishstiz.minecraftcursor.registry.widget.CursorTextWidgetRegistry;
import io.github.fishstiz.minecraftcursor.registry.widget.WorldListWidgetCursorRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.EntryListWidget;

public class WidgetCursorRegistry extends CursorTypeRegistry {
    private final CursorPointerWidgetRegistry pointerRegistry;
    private final CursorTextWidgetRegistry textRegistry;

    public WidgetCursorRegistry() {
        registerParentElement(EntryListWidget.class);
        this.pointerRegistry = new CursorPointerWidgetRegistry(this);
        this.textRegistry = new CursorTextWidgetRegistry(this);
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

    public void registerParentElement(Class<? extends ParentElement> parentElement) {
        this.register(parentElement, this::parentElementGetChildCursorType);
    }

    public CursorType parentElementGetChildCursorType(Element parentElement, double mouseX, double mouseY, float delta) {
        for (Element child : ((ParentElement) parentElement).children()) {
            if (child.isMouseOver(mouseX, mouseY)) {
                return getCursorType(child, mouseX, mouseY, delta);
            }
        }
        return CursorType.DEFAULT;
    }
}
