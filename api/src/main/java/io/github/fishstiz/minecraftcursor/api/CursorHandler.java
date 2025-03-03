package io.github.fishstiz.minecraftcursor.api;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import net.minecraft.client.gui.components.events.GuiEventListener;

/**
 * This interface defines a handler for determining the {@link CursorType} of an {@link GuiEventListener}.
 *
 * <p>Must be registered using the {@link ElementRegistrar#register(CursorHandler)} method to work.</p>
 *
 * @param <T> the type of the {@link GuiEventListener} the cursor handler is associated with.
 *            <br><br>
 *            If the target {@link GuiEventListener} is inaccessible, you can pass {@link GuiEventListener}
 *            as a generic type and override the {@link #getTargetElement()} method to return a {@link TargetElement}
 *            with the fully qualified class name (FQCN) of the element.
 */
public interface CursorHandler<T extends GuiEventListener> {
    /**
     * Returns the target element associated with this cursor handler.
     * The target element is determined either by the element class or the fully qualified class name.
     *
     * @return a {@link TargetElement} containing either the element class or its fully qualified class name
     */
    @SuppressWarnings("unchecked")
    default @NotNull TargetElement<T> getTargetElement() {
        TypeToken<T> typeToken = new TypeToken<>(getClass()) {
        };
        return TargetElement.fromClass((Class<T>) typeToken.getRawType());
    }

    /**
     * Retrieves the cursor type to be applied when the mouse is over the target element.
     *
     * @param element the element the cursor is hovering over
     * @param mouseX  the X coordinate of the mouse
     * @param mouseY  the Y coordinate of the mouse
     * @return the {@link CursorType} to be applied
     */
    CursorType getCursorType(T element, double mouseX, double mouseY);

    /**
     * The record that represents the target element of the {@link CursorHandler}.
     * <p>
     * It stores either the {@link Class} of the target element or a {@link String} representing
     * its fully qualified class name (FQCN).
     * </p>
     *
     * <p>The fully qualified class name can be used when the target element is inaccessible, allowing
     * for reflection-based access to the class.</p>
     *
     * @param <T>                     the type of the {@link GuiEventListener}
     * @param elementClass            the {@link Optional} class of the target element
     * @param fullyQualifiedClassName the {@link Optional} fully qualified class name of the target element
     */
    record TargetElement<T extends GuiEventListener>(
            Optional<Class<T>> elementClass,
            Optional<String> fullyQualifiedClassName
    ) {
        /**
         * Creates a {@link TargetElement} from the given element class.
         *
         * @param elementClass the class of the target element
         * @param <T>          the type of the {@link GuiEventListener}
         * @return a {@link TargetElement} containing the element class
         */
        public static <T extends GuiEventListener> TargetElement<T> fromClass(Class<T> elementClass) {
            return new TargetElement<>(Optional.of(elementClass), Optional.empty());
        }

        /**
         * Creates a {@link TargetElement} from the given fully qualified class name.
         *
         * <p>Use the intermediary mappings for native Minecraft elements.</p>
         *
         * @param fullyQualifiedClassName the fully qualified class name of the target element
         * @param <T>                     the type of the {@link GuiEventListener}
         * @return a {@link TargetElement} containing the fully qualified class name for reflection
         */
        public static <T extends GuiEventListener> TargetElement<T> fromClassName(String fullyQualifiedClassName) {
            return new TargetElement<>(Optional.empty(), Optional.of(fullyQualifiedClassName));
        }
    }
}
