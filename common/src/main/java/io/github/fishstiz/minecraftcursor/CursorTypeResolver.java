package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.ElementRegistrar;
import io.github.fishstiz.minecraftcursor.inspect.ElementInspector;
import io.github.fishstiz.minecraftcursor.platform.Services;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

class CursorTypeResolver implements ElementRegistrar {
    private final List<ElementEntry<? extends GuiEventListener>> registry = new ArrayList<>();
    private final HashMap<String, CursorTypeFunction<? extends GuiEventListener>> cachedRegistry = new HashMap<>();
    private ElementInspector inspector;
    String lastFailedElement;

    CursorTypeResolver() {
        inspector = new ElementInspector() {};
    }

    @Override
    public <T extends GuiEventListener> void register(CursorHandler<T> cursorHandler) {
        CursorHandler.TargetElement<T> targetElement = cursorHandler.getTargetElement();

        if (targetElement.elementClass().isPresent()) {
            register(targetElement.elementClass().get(), cursorHandler::getCursorType);
        } else if (targetElement.fullyQualifiedClassName().isPresent()) {
            register(targetElement.fullyQualifiedClassName().get(), cursorHandler::getCursorType);
        } else {
            throw new NullPointerException("Could not register cursor handler: "
                                           + cursorHandler.getClass().getName()
                                           + " - Target Element Class and FQCN not present");
        }
    }

    @Override
    public <T extends GuiEventListener> void register(String binaryName, CursorTypeFunction<T> elementToCursorType) {
        try {
            @SuppressWarnings("unchecked")
            Class<T> elementClass = (Class<T>) Class.forName(Services.PLATFORM.mapClassName("intermediary", binaryName));
            if (!GuiEventListener.class.isAssignableFrom(elementClass)) {
                throw new ClassCastException(binaryName + " is not a subclass of Element");
            }
            register(elementClass, elementToCursorType);
        } catch (ClassNotFoundException e) {
            MinecraftCursor.LOGGER.error("[minecraft-cursor] Error registering element. Class not found: {}", binaryName);
        } catch (ClassCastException e) {
            MinecraftCursor.LOGGER.error("[minecraft-cursor] Error registering element. Invalid class: {}", e.getMessage());
        }
    }

    @Override
    public <T extends GuiEventListener> void register(Class<T> elementClass, CursorTypeFunction<T> elementToCursorType) {
        registry.add(new ElementEntry<>(elementClass, elementToCursorType));
    }


    @SuppressWarnings("unchecked")
    public <T extends GuiEventListener> CursorType resolve(T element, double mouseX, double mouseY) {
        String elementName = element.getClass().getName();

        try {
            if (element instanceof CursorProvider cursorProvider) {
                CursorType providedCursorType = cursorProvider.getCursorType(mouseX, mouseY);
                if (providedCursorType != null && !providedCursorType.isDefault()) {
                    return providedCursorType;
                }
            }
            CursorTypeFunction<T> mapper = (CursorTypeFunction<T>) cachedRegistry.get(elementName);

            if (mapper == null) {
                mapper = (CursorTypeFunction<T>) resolveMapper(element);
                if (!inspector.setHovered(element, true)) {
                    cachedRegistry.put(elementName, mapper);
                }
            }

            return mapper.getCursorType(element, mouseX, mouseY);
        } catch (LinkageError | Exception e) {
            if (!elementName.equals(lastFailedElement)) {
                lastFailedElement = elementName;
                MinecraftCursor.LOGGER.error(
                        "Could not get cursor type for element: {}",
                        Services.PLATFORM.unmapClassName("intermediary", elementName)
                );
            }
        }
        return CursorType.DEFAULT;
    }

    private CursorTypeFunction<? extends GuiEventListener> resolveMapper(GuiEventListener element) {
        for (int i = registry.size() - 1; i >= 0; i--) {
            if (registry.get(i).element.isInstance(element)) {
                inspector.setHovered(element, true);
                return registry.get(i).mapper;
            }
        }
        if (element instanceof ContainerEventHandler) {
            return (CursorTypeFunction<ContainerEventHandler>) this::resolveChild;
        }
        return ElementRegistrar::elementToDefault;
    }

    private <T extends ContainerEventHandler> CursorType resolveChild(T parent, double mouseX, double mouseY) {
        Optional<GuiEventListener> child = parent.getChildAt(mouseX, mouseY);
        if (child.isPresent()) {
            GuiEventListener hoveredElement = child.get();
            if (hoveredElement instanceof ContainerEventHandler nestedParent) {
                CursorType cursorType = resolveChild(nestedParent, mouseX, mouseY);
                if (!cursorType.isDefault()) return cursorType;
            }
            inspector.setHovered(hoveredElement, false);
            return resolve(hoveredElement, mouseX, mouseY);
        }
        return CursorType.DEFAULT;
    }

    public ElementInspector getInspector() {
        return inspector;
    }

    public void toggleInspector() {
        inspector = ElementInspector.toggle(inspector);
        cachedRegistry.clear();
    }

    record ElementEntry<T extends GuiEventListener>(Class<T> element, CursorTypeFunction<T> mapper) {
    }
}
