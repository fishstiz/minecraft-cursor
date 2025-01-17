package io.github.fishstiz.minecraftcursor.registry;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorHotspotWidget;
import io.github.fishstiz.minecraftcursor.registry.gui.ingame.CreativeInventoryScreenCursor;
import io.github.fishstiz.minecraftcursor.registry.gui.modmenu.ModMenuWidgetsCursor;
import io.github.fishstiz.minecraftcursor.registry.gui.modmenu.ModScreenCursor;
import io.github.fishstiz.minecraftcursor.registry.gui.ingame.recipebook.RecipeBookScreenCursor;
import io.github.fishstiz.minecraftcursor.registry.gui.world.WorldListWidgetCursor;
import io.github.fishstiz.minecraftcursor.registry.utils.ElementCursorTypeFunction;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CursorTypeRegistry {
    private final List<AbstractMap.SimpleImmutableEntry<Class<? extends Element>, ElementCursorTypeFunction>> registry = new ArrayList<>();
    private final ConcurrentHashMap<String, ElementCursorTypeFunction> cachedRegistry = new ConcurrentHashMap<>();

    public CursorTypeRegistry() {
        init();
    }

    public void init() {
        initPointerElements();
        initTextElements();
        initGuis();
    }

    public void initPointerElements() {
        register(PressableWidget.class, CursorTypeRegistry::pressableWidgetCursor);
        register(SliderWidget.class, CursorTypeRegistry::sliderWidgetCursor);
        register(SelectedCursorHotspotWidget.class, CursorTypeRegistry::elementToPointer);
    }

    public void initTextElements() {
        register(TextFieldWidget.class, CursorTypeRegistry::textFieldWidgetCursor);
    }

    public void initGuis() {
        WorldListWidgetCursor.register(this);
        RecipeBookScreenCursor.register(this);
        CreativeInventoryScreenCursor.register(this);

        try {
            if (FabricLoader.getInstance().isModLoaded("modmenu")) {
                ModMenuWidgetsCursor.register(this);
                ModScreenCursor.register(this);
            }
        } catch (NoClassDefFoundError ignore) {
        }
    }

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
            MinecraftCursor.LOGGER.error("Error registering widget cursor type. Class not found: {}", fullyQualifiedClassName);
        }
    }

    public void register(Class<? extends Element> elementClass, ElementCursorTypeFunction elementToCursorType) {
        registry.add(new AbstractMap.SimpleImmutableEntry<>(elementClass, elementToCursorType));
    }

    public CursorType getCursorType(Element element, double mouseX, double mouseY) {
        return cachedRegistry.computeIfAbsent(element.getClass().getName(), k -> computeCursorType(element)).apply(element, mouseX, mouseY);
    }

    private ElementCursorTypeFunction computeCursorType(Element element) {
        for (int i = registry.size() - 1; i >= 0; i--) {
            if (registry.get(i).getKey().isInstance(element)) {
                return registry.get(i).getValue();
            }
        }
        if (element instanceof ParentElement) {
            return this::parentElementGetChildCursorType;
        }
        return CursorTypeRegistry::elementToDefault;
    }

    public CursorType parentElementGetChildCursorType(Element parentElement, double mouseX, double mouseY) {
        for (Element child : ((ParentElement) parentElement).children()) {
            if (child instanceof ParentElement) {
                parentElementGetChildCursorType(child, mouseX, mouseY);
            }
            if (child.isMouseOver(mouseX, mouseY)) {
                return getCursorType(child, mouseX, mouseY);
            }
        }
        return CursorType.DEFAULT;
    }

    public static CursorType elementToDefault(Element ignoreElement, double ignoreMouseX, double ignoreMouseY) {
        return CursorType.DEFAULT;
    }

    public static CursorType elementToPointer(Element ignoreElement, double ignoreMouseX, double ignoreMouseY) {
        return CursorType.POINTER;
    }

    public static CursorType elementToText(Element ignoreElement, double ignoreMouseX, double ignoreMouseY) {
        return CursorType.TEXT;
    }

    private static CursorType pressableWidgetCursor(Element entry, double mouseX, double mouseY) {
        PressableWidget button = (PressableWidget) entry;
        return button.isHovered() && button.active && button.visible ?
                CursorType.POINTER : CursorType.DEFAULT;
    }

    private static CursorType sliderWidgetCursor(Element element, double mouseX, double mouseY) {
        SliderWidget slider = (SliderWidget) element;
        return slider.active && slider.visible && slider.isHovered() ?
                CursorType.POINTER : CursorType.DEFAULT;
    }

    private static CursorType textFieldWidgetCursor(Element element, double mouseX, double mouseY) {
        TextFieldWidget textField = (TextFieldWidget) element;
        return textField.visible && textField.isHovered() ? CursorType.TEXT : CursorType.DEFAULT;
    }
}
