package io.github.fishstiz.minecraftcursor.registry;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorHotspotWidget;
import io.github.fishstiz.minecraftcursor.registry.gui.ingame.*;
import io.github.fishstiz.minecraftcursor.registry.gui.modmenu.ModScreenCursor;
import io.github.fishstiz.minecraftcursor.registry.gui.multiplayer.MultiplayerServerListWidgetCursor;
import io.github.fishstiz.minecraftcursor.registry.gui.world.WorldListWidgetCursor;
import io.github.fishstiz.minecraftcursor.utils.CursorTypeUtils;
import io.github.fishstiz.minecraftcursor.registry.utils.ElementCursorTypeFunction;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TabButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.fishstiz.minecraftcursor.registry.utils.LookupUtils.NAMESPACE;
import static io.github.fishstiz.minecraftcursor.registry.utils.LookupUtils.RESOLVER;

public class CursorTypeRegistry {
    private final List<AbstractMap.SimpleImmutableEntry<Class<? extends Element>, ElementCursorTypeFunction>> registry = new ArrayList<>();
    private final ConcurrentHashMap<String, ElementCursorTypeFunction> cachedRegistry = new ConcurrentHashMap<>();

    public CursorTypeRegistry() {
        init();
    }

    public void init() {
        initElements();
        initGuis();
    }

    public void initElements() {
        register(PressableWidget.class, CursorTypeRegistry::clickableWidgetCursor);
        register(TabButtonWidget.class, CursorTypeRegistry::tabButtonWidgetCursor);
        register(SliderWidget.class, CursorTypeRegistry::sliderWidgetCursor);
        register(SelectedCursorHotspotWidget.class, CursorTypeRegistry::hotspotWidgetCursor);
        register(TextFieldWidget.class, CursorTypeRegistry::textFieldWidgetCursor);
    }

    public void initGuis() {
        HandledScreenCursor.register(this);
        WorldListWidgetCursor.register(this);
        MultiplayerServerListWidgetCursor.register(this);
        RecipeBookScreenCursor.register(this);
        CreativeInventoryScreenCursor.register(this);
        BookEditScreenCursor.register(this);
        EnchantmentScreenCursor.register(this);
        StonecutterScreenCursor.register(this);
        LoomScreenCursor.register(this);
        CrafterScreenCursor.register(this);
        MerchantScreen$WidgetButtonPage.register(this);
        AdvancementScreenCursor.register(this);

        try {
            if (FabricLoader.getInstance().isModLoaded("modmenu")) {
                ModScreenCursor.register(this);
            }
        } catch (NoClassDefFoundError ignore) {
            MinecraftCursor.LOGGER.warn("Could not register cursor type for Mod Menu");
        }
    }

    @SuppressWarnings("unchecked")
    public void register(String fullyQualifiedClassName, ElementCursorTypeFunction elementToCursorType) {
        try {
            Class<?> elementClass = Class.forName(RESOLVER.mapClassName(NAMESPACE, fullyQualifiedClassName));

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
        try {
            return cachedRegistry.computeIfAbsent(element.getClass().getName(), k -> computeCursorType(element)).apply(element, mouseX, mouseY);
        } catch (Exception e) {
            MinecraftCursor.LOGGER.warn("Could not get cursor type for element: {}",
                    RESOLVER.unmapClassName("named", element.getClass().getName()));
        }
        return CursorType.DEFAULT;
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
        CursorType cursorType = CursorType.DEFAULT;
        for (Element child : ((ParentElement) parentElement).children()) {
            if (child instanceof ParentElement) {
                CursorType parentCursorType = parentElementGetChildCursorType(child, mouseX, mouseY);
                cursorType = parentCursorType != CursorType.DEFAULT ? parentCursorType : cursorType;
            }
            if (child.isMouseOver(mouseX, mouseY)) {
                CursorType childCursorType = getCursorType(child, mouseX, mouseY);
                cursorType = childCursorType != CursorType.DEFAULT ? childCursorType : cursorType;
            }
        }
        return cursorType;
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

    private static CursorType clickableWidgetCursor(Element element, double mouseX, double mouseY) {
        ClickableWidget button = (ClickableWidget) element;
        return button.active && button.visible ?
                CursorType.POINTER : CursorType.DEFAULT;
    }

    private static CursorType tabButtonWidgetCursor(Element element, double mouseX, double mouseY) {
        TabButtonWidget button = (TabButtonWidget) element;
        return button.active && button.visible && !button.isCurrentTab() ?
                CursorType.POINTER : CursorType.DEFAULT;
    }

    private static CursorType sliderWidgetCursor(Element element, double mouseX, double mouseY) {
        SliderWidget slider = (SliderWidget) element;
        if (slider.isFocused() && (CursorTypeUtils.isLeftClickHeld() || CursorTypeUtils.isGrabbing())) {
            return CursorType.GRABBING;
        }
        return slider.active && slider.visible ?
                CursorType.POINTER : CursorType.DEFAULT;
    }

    private static CursorType textFieldWidgetCursor(Element element, double mouseX, double mouseY) {
        TextFieldWidget textField = (TextFieldWidget) element;
        return textField.visible ? CursorType.TEXT : CursorType.DEFAULT;
    }

    private static CursorType hotspotWidgetCursor(Element element, double mouseX, double mouseY) {
        if (CursorTypeUtils.isLeftClickHeld() || CursorTypeUtils.isGrabbing()) {
            return CursorType.GRABBING;
        }
        return CursorType.POINTER;
    }
}
