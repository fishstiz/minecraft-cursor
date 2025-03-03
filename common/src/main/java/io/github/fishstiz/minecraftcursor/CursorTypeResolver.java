package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.ElementRegistrar;
import io.github.fishstiz.minecraftcursor.platform.Services;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

final class CursorTypeResolver implements ElementRegistrar {
    static final CursorTypeResolver INSTANCE = new CursorTypeResolver();
    private final List<AbstractMap.SimpleImmutableEntry<Class<? extends GuiEventListener>,
            CursorTypeFunction<? extends GuiEventListener>>>
            registry = new ArrayList<>();
    private final ConcurrentHashMap<String, CursorTypeFunction<? extends GuiEventListener>>
            cachedRegistry = new ConcurrentHashMap<>();
    private String lastFailedElement;

    private CursorTypeResolver() {
    }

    @Override
    public <T extends GuiEventListener> void register(CursorHandler<T> cursorHandler) {
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
    public <T extends GuiEventListener> void register(String fullyQualifiedClassName, CursorTypeFunction<T> elementToCursorType) {
        try {
            @SuppressWarnings("unchecked")
            Class<T> elementClass = (Class<T>) Class.forName(Services.PLATFORM.mapClassName("intermediary", fullyQualifiedClassName));
            if (!GuiEventListener.class.isAssignableFrom(elementClass)) {
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
    public <T extends GuiEventListener> void register(Class<T> elementClass, CursorTypeFunction<T> elementToCursorType) {
        registry.add(new AbstractMap.SimpleImmutableEntry<>(elementClass, elementToCursorType));
    }

    public <T extends GuiEventListener> CursorType getCursorType(T element, double mouseX, double mouseY) {
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
        } catch (LinkageError | Exception e) {
            String failedElement = element.getClass().getName();
            if (!failedElement.equals(lastFailedElement)) {
                lastFailedElement = failedElement;
                MinecraftCursor.LOGGER.error(
                        "Could not get cursor type for element: {}",
                        Services.PLATFORM.unmapClassName("named", failedElement)
                );
            }
        }
        return CursorType.DEFAULT;
    }

    private CursorTypeFunction<? extends GuiEventListener> computeCursorType(GuiEventListener element) {
        for (int i = registry.size() - 1; i >= 0; i--) {
            if (registry.get(i).getKey().isInstance(element)) {
                return registry.get(i).getValue();
            }
        }
        if (element instanceof ContainerEventHandler) {
            return (CursorTypeFunction<ContainerEventHandler>) this::getChildCursorType;
        }
        return ElementRegistrar::elementToDefault;
    }

    private <T extends ContainerEventHandler> CursorType getChildCursorType(T parentElement, double mouseX, double mouseY) {
        CursorType cursorType = CursorType.DEFAULT;
        for (GuiEventListener child : parentElement.children()) {
            if (child instanceof ContainerEventHandler childParent) {
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
}
