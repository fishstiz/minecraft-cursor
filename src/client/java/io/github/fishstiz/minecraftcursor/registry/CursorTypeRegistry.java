package io.github.fishstiz.minecraftcursor.registry;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.widgets.EntryListWidgetCursorRegistry;
import io.github.fishstiz.minecraftcursor.registry.widgets.CursorPointerWidgetRegistry;
import io.github.fishstiz.minecraftcursor.registry.widgets.CursorTextWidgetRegistry;
import io.github.fishstiz.minecraftcursor.registry.widgets.WorldListWidgetCursorRegistry;
import net.minecraft.client.gui.Element;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CursorTypeRegistry {
    private static final List<Map.Entry<Class<? extends Element>, ElementCursorTypeFunction>> registry = new ArrayList<>();
    private static final ConcurrentHashMap<String, ElementCursorTypeFunction> cachedRegistry = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static void register(String fullyQualifiedClassName, ElementCursorTypeFunction elementToCursorType) {
        try {
            Class<?> widgetClass = Class.forName(fullyQualifiedClassName);

            assert Element.class.isAssignableFrom(widgetClass) :
                    fullyQualifiedClassName + " is not an instance of net.minecraft.client.gui.Element";

            if (Element.class.isAssignableFrom(widgetClass)) {
                register((Class<? extends Element>) widgetClass, elementToCursorType);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + fullyQualifiedClassName, e);
        }
    }

    public static void register(Class<? extends Element> widgetClass, ElementCursorTypeFunction elementToCursorType) {
        registry.add(new AbstractMap.SimpleEntry<>(widgetClass, elementToCursorType));
    }

    public static CursorType getCursorType(Element element, double mouseX, double mouseY, float delta) {
        return cachedRegistry.computeIfAbsent(element.getClass().getName(), k -> computeCursorType(element)).apply(element, mouseX, mouseY, delta);
    }

    private static ElementCursorTypeFunction computeCursorType(Element element) {
        for (int i = registry.size() - 1; i >= 0; i--) {
            if (registry.get(i).getKey().isInstance(element)) {
                return registry.get(i).getValue();
            }
        }
        return CursorTypeRegistry::elementToDefault;
    }

    public static void init() {
        CursorPointerWidgetRegistry.init();
        CursorTextWidgetRegistry.init();
        EntryListWidgetCursorRegistry.init();
        WorldListWidgetCursorRegistry.init();
    }

    public static CursorType elementToDefault(Element ignoreElement, double ignoreMouseX, double ignoreMouseY, float ignoreDelta) {
        return CursorType.DEFAULT;
    }

    public static CursorType elementToPointer(Element ignoreElement, double ignoreMouseX, double ignoreMouseY, float ignoreDelta) {
        return CursorType.POINTER;
    }

    public static CursorType elementToText(Element ignoreElement, double ignoreMouseX, double ignoreMouseY, float ignoreDelta) {
        return CursorType.TEXT;
    }
}
