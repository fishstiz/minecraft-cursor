package io.github.fishstiz.minecraftcursor.registry;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import net.minecraft.client.gui.Element;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CursorTypeRegistry {
    private final List<AbstractMap.SimpleImmutableEntry<Class<? extends Element>, ElementCursorTypeFunction>> registry = new ArrayList<>();
    private final ConcurrentHashMap<String, ElementCursorTypeFunction> cachedRegistry = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public void register(String fullyQualifiedClassName, ElementCursorTypeFunction elementToCursorType) {
        try {
            Class<?> elementClass = Class.forName(fullyQualifiedClassName);

            assert Element.class.isAssignableFrom(elementClass) :
                    fullyQualifiedClassName + " is not an instance of net.minecraft.client.gui.Element";

            if (Element.class.isAssignableFrom(elementClass)) {
                register((Class<? extends Element>) elementClass, elementToCursorType);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + fullyQualifiedClassName, e);
        }
    }

    public void register(Class<? extends Element> elementClass, ElementCursorTypeFunction elementToCursorType) {
        registry.add(new AbstractMap.SimpleImmutableEntry<>(elementClass, elementToCursorType));
    }

    public CursorType getCursorType(Element element, double mouseX, double mouseY, float delta) {
        return cachedRegistry.computeIfAbsent(element.getClass().getName(), k -> computeCursorType(element)).apply(element, mouseX, mouseY, delta);
    }

    private ElementCursorTypeFunction computeCursorType(Element element) {
        for (int i = registry.size() - 1; i >= 0; i--) {
            if (registry.get(i).getKey().isInstance(element)) {
                return registry.get(i).getValue();
            }
        }
        return CursorTypeRegistry::elementToDefault;
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
