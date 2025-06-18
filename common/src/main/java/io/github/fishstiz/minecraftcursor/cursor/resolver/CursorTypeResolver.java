package io.github.fishstiz.minecraftcursor.cursor.resolver;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.ElementRegistrar;
import io.github.fishstiz.minecraftcursor.cursor.handler.InternalCursorProvider;
import io.github.fishstiz.minecraftcursor.platform.Services;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.*;

public final class CursorTypeResolver implements ElementRegistrar {
    public static final CursorTypeResolver INSTANCE = new CursorTypeResolver();
    private final List<ElementEntry<?>> registry = new ArrayList<>();
    private final Map<Class<?>, CursorTypeFunction<?>> cache = new Object2ObjectOpenHashMap<>();
    private ElementInspector inspector = ElementInspector.NO_OP;
    private String lastFailedElement;

    private CursorTypeResolver() {
    }

    @Override
    public <T extends GuiEventListener> void register(CursorHandler<T> cursorHandler) {
        CursorHandler.TargetElement<T> targetElement = cursorHandler.getTargetElement();
        if (targetElement instanceof CursorHandler.TargetElement.ClassRef<T> classRef) {
            register(classRef.elementClass(), cursorHandler);
        } else if (targetElement instanceof CursorHandler.TargetElement.NameRef<T> nameRef) {
            register(nameRef.className(), cursorHandler);
        }
    }

    @Override
    public <T extends GuiEventListener> void register(String className, CursorTypeFunction<T> cursorTypeFunction) {
        try {
            @SuppressWarnings("unchecked")
            Class<T> elementClass = (Class<T>) Class.forName(Services.PLATFORM.mapClassName("intermediary", className));
            if (!GuiEventListener.class.isAssignableFrom(elementClass)) {
                throw new ClassCastException(className + " is not a subclass of ElementView");
            }
            register(elementClass, cursorTypeFunction);
        } catch (ClassNotFoundException e) {
            MinecraftCursor.LOGGER.error("[minecraft-cursor] Error registering element. Class not found: {}", className);
        } catch (ClassCastException e) {
            MinecraftCursor.LOGGER.error("[minecraft-cursor] Error registering element. Invalid class: {}", e.getMessage());
        }
    }

    @Override
    public <T extends GuiEventListener> void register(Class<T> elementClass, CursorTypeFunction<T> cursorTypeFunction) {
        registry.add(new ElementEntry<>(Objects.requireNonNull(elementClass), Objects.requireNonNull(cursorTypeFunction)));
    }

    public CursorType resolve(GuiEventListener element, double mouseX, double mouseY) {
        Class<?> elementClass = element.getClass();
        try {
            if (element instanceof CursorProvider cursorProvider) {
                CursorType providedCursorType = cursorProvider.getCursorType(mouseX, mouseY);
                if (providedCursorType != null && !providedCursorType.isDefault()) {
                    return providedCursorType;
                }
            }

            CursorTypeFunction<?> mapper = cache.get(elementClass);
            if (mapper == null) {
                mapper = resolveMapper(element);
                if (!inspector.setProcessed(element, true)) {
                    cache.put(elementClass, mapper);
                }
            }

            @SuppressWarnings("unchecked")
            CursorType mapped = ((CursorTypeFunction<GuiEventListener>) mapper).getCursorType(element, mouseX, mouseY);
            if (mapped != null && !mapped.isDefault()) {
                return mapped;
            }

            if (element instanceof InternalCursorProvider internalCursorProvider) {
                inspector.setProcessed(element, false);
                return internalCursorProvider.minecraft_cursor$getCursorType(mouseX, mouseY);
            }

            return CursorType.DEFAULT;
        } catch (LinkageError | Exception e) {
            String elementName = elementClass.getName();
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
                inspector.setProcessed(element, true);
                return registry.get(i).mapper;
            }
        }
        if (element instanceof ContainerEventHandler) {
            return (CursorTypeFunction<ContainerEventHandler>) this::resolveParent;
        }
        return ElementRegistrar::elementToDefault;
    }

    private CursorType resolveParent(ContainerEventHandler parent, double mouseX, double mouseY) {
        return ElementWalker.walk(parent, mouseX, mouseY, this::resolveChild, type -> !type.isDefault(), CursorType.DEFAULT);
    }

    private CursorType resolveChild(GuiEventListener child, double mouseX, double mouseY) {
        this.inspector.setProcessed(child, false);
        return resolve(child, mouseX, mouseY);
    }

    public int cacheSize() {
        return this.cache.size();
    }

    public void clearCache() {
        this.cache.clear();
    }

    public ElementInspector getInspector() {
        return inspector;
    }

    public void toggleInspector() {
        inspector.destroy();
        inspector = this.inspector == ElementInspector.NO_OP ? new ElementInspectorImpl() : ElementInspector.NO_OP;
        cache.clear();
    }

    private record ElementEntry<T extends GuiEventListener>(Class<T> element, CursorTypeFunction<T> mapper) {
    }
}
