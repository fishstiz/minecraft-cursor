package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.ElementRegistrar;
import io.github.fishstiz.minecraftcursor.cursorhandler.ingame.*;
import io.github.fishstiz.minecraftcursor.cursorhandler.modmenu.ModScreenCursorHandler;
import io.github.fishstiz.minecraftcursor.cursorhandler.multiplayer.MultiplayerServerListWidgetCursorHandler;
import io.github.fishstiz.minecraftcursor.cursorhandler.world.WorldListWidgetCursorHandler;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.*;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.fishstiz.minecraftcursor.util.LookupUtil.NAMESPACE;
import static io.github.fishstiz.minecraftcursor.util.LookupUtil.RESOLVER;

class CursorTypeResolver implements ElementRegistrar {
    private final List<AbstractMap.SimpleImmutableEntry<Class<? extends Element>,
            CursorTypeFunction<? extends Element>>>
            registry = new ArrayList<>();
    private final ConcurrentHashMap<String, CursorTypeFunction<? extends Element>>
            cachedRegistry = new ConcurrentHashMap<>();
    private String lastFailedElement;

    CursorTypeResolver() {
        init();
    }

    private void init() {
        initElements();
        initCursorHandlers();
    }

    private void initElements() {
        register(PressableWidget.class, CursorTypeResolver::clickableWidgetCursor);
        register(TabButtonWidget.class, CursorTypeResolver::tabButtonWidgetCursor);
        register(SliderWidget.class, CursorTypeResolver::sliderWidgetCursor);
        register(TextFieldWidget.class, CursorTypeResolver::textFieldWidgetCursor);
    }

    private void initCursorHandlers() {
        register(new WorldListWidgetCursorHandler());
        register(new MultiplayerServerListWidgetCursorHandler());
        register(new HandledScreenCursorHandler<>());
        register(RecipeBookScreenCursorHandler.INVENTORY);
        register(RecipeBookScreenCursorHandler.CRAFTING);
        register(RecipeBookScreenCursorHandler.FURNACE);
        register(new CreativeInventoryScreenCursorHandler());
        register(new BookEditScreenCursorHandler());
        register(new EnchantmentScreenCursorHandler());
        register(new StonecutterScreenCursorHandler());
        register(new LoomScreenCursorHandler());
        register(new CrafterScreenCursorHandler());
        register(new MerchantScreenButtonCursorHandler());
        register(new AdvancementsScreenCursorHandler());

        try {
            if (FabricLoader.getInstance().isModLoaded("modmenu")) {
                register(new ModScreenCursorHandler());
                register("com.terraformersmc.modmenu.gui.widget.DescriptionListWidget$MojangCreditsEntry", ElementRegistrar::elementToPointer);
                register("com.terraformersmc.modmenu.gui.widget.DescriptionListWidget$LinkEntry", ElementRegistrar::elementToPointer);
            }
        } catch (NoClassDefFoundError e) {
            MinecraftCursor.LOGGER.warn("Could not register cursor type for Mod Menu");
        }
    }

    @Override
    public <T extends Element> void register(CursorHandler<T> cursorHandler) {
        CursorHandler.TargetElement<T> targetElement = cursorHandler.getTargetElement();

        if (targetElement.elementClass().isPresent()) {
            register(targetElement.elementClass().get(), cursorHandler::getCursorType);
        } else if (targetElement.fullyQualifiedClassName().isPresent()) {
            register(targetElement.fullyQualifiedClassName().get(), cursorHandler::getCursorType);
        } else {
            throw new NoClassDefFoundError("Could not register cursor handler: "
                    + cursorHandler.getClass().getName()
                    + " - Target Element Class and FQCN not present");
        }
    }

    @Override
    public <T extends Element> void register(String fullyQualifiedClassName, CursorTypeFunction<T> elementToCursorType) {
        try {
            @SuppressWarnings("unchecked")
            Class<T> elementClass = (Class<T>) Class.forName(RESOLVER.mapClassName(NAMESPACE, fullyQualifiedClassName));
            if (!Element.class.isAssignableFrom(elementClass)) {
                throw new ClassCastException(fullyQualifiedClassName + " is not a subclass of Element");
            }
            register(elementClass, elementToCursorType);
        } catch (ClassNotFoundException e) {
            MinecraftCursor.LOGGER.error("Error registering element. Class not found: {}", fullyQualifiedClassName);
        } catch (ClassCastException e) {
            MinecraftCursor.LOGGER.error("Error registering element. Invalid class: {}", e.getMessage());
        }
    }

    @Override
    public <T extends Element> void register(Class<T> elementClass, CursorTypeFunction<T> elementToCursorType) {
        registry.add(new AbstractMap.SimpleImmutableEntry<>(elementClass, elementToCursorType));
    }

    public <T extends Element> CursorType getCursorType(T element, double mouseX, double mouseY) {
        try {
            if (element instanceof CursorProvider cursorProvider) {
                CursorType providedCursorType = cursorProvider.getCursorType(mouseX, mouseY);
                if (providedCursorType != null && providedCursorType != CursorType.DEFAULT) {
                    return providedCursorType;
                }
            }
            @SuppressWarnings("unchecked")
            CursorType cursorType = ((CursorTypeFunction<T>) cachedRegistry
                    .computeIfAbsent(element.getClass().getName(), k -> computeCursorType(element)))
                    .getCursorType(element, mouseX, mouseY);
            return cursorType != null ? cursorType : CursorType.DEFAULT;
        } catch (Exception e) {
            String failedElement = element.getClass().getName();
            if (!failedElement.equals(lastFailedElement)) {
                lastFailedElement = failedElement;
                MinecraftCursor.LOGGER.error(
                        "Could not get cursor type for element: {}",
                        RESOLVER.unmapClassName("named", failedElement)
                );
            }
        }
        return CursorType.DEFAULT;
    }

    private CursorTypeFunction<? extends Element> computeCursorType(Element element) {
        for (int i = registry.size() - 1; i >= 0; i--) {
            if (registry.get(i).getKey().isInstance(element)) {
                return registry.get(i).getValue();
            }
        }
        if (element instanceof ParentElement) {
            return (CursorTypeFunction<ParentElement>) this::getChildCursorType;
        }
        return ElementRegistrar::elementToDefault;
    }

    private <T extends ParentElement> CursorType getChildCursorType(T parentElement, double mouseX, double mouseY) {
        CursorType cursorType = CursorType.DEFAULT;
        for (Element child : parentElement.children()) {
            if (child instanceof ParentElement childParent) {
                CursorType parentCursorType = getChildCursorType(childParent, mouseX, mouseY);
                cursorType = parentCursorType != CursorType.DEFAULT ? parentCursorType : cursorType;
            }
            if (child.isMouseOver(mouseX, mouseY)) {
                CursorType childCursorType = getCursorType(child, mouseX, mouseY);
                cursorType = childCursorType != CursorType.DEFAULT ? childCursorType : cursorType;
            }
        }
        return cursorType;
    }

    private static <T extends ClickableWidget> CursorType clickableWidgetCursor(T clickable, double mouseX, double mouseY) {
        return clickable.active && clickable.visible ? CursorType.POINTER : CursorType.DEFAULT;
    }

    private static <T extends TabButtonWidget> CursorType tabButtonWidgetCursor(T tabButton, double mouseX, double mouseY) {
        return tabButton.active && tabButton.visible && !tabButton.isCurrentTab() ?
                CursorType.POINTER : CursorType.DEFAULT;
    }

    private static <T extends SliderWidget> CursorType sliderWidgetCursor(T slider, double mouseX, double mouseY) {
        if (slider.isFocused() && (CursorTypeUtil.isLeftClickHeld() || CursorTypeUtil.isGrabbing())) {
            return CursorType.GRABBING;
        }
        return slider.active && slider.visible ? CursorType.POINTER : CursorType.DEFAULT;
    }

    private static <T extends TextFieldWidget> CursorType textFieldWidgetCursor(T textField, double mouseX, double mouseY) {
        return textField.visible ? CursorType.TEXT : CursorType.DEFAULT;
    }
}
