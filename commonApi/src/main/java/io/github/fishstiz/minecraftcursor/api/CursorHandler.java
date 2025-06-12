package io.github.fishstiz.minecraftcursor.api;

import com.google.common.reflect.TypeToken;
import io.github.fishstiz.minecraftcursor.api.ElementRegistrar.CursorTypeFunction;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.components.events.GuiEventListener;

/**
 * This interface defines a {@link CursorTypeFunction} for a target {@link GuiEventListener}.
 *
 * <p>Must be registered using {@link ElementRegistrar#register(CursorHandler)}.</p>
 *
 * @param <T> the type of the {@link GuiEventListener} the cursor handler is associated with.
 *            <br><br>
 *            If the target {@link GuiEventListener} is inaccessible, you can pass {@link GuiEventListener}
 *            as a generic type and override the {@link #getTargetElement()} method to return a {@link TargetElement}
 *            with the binary name of the element.
 */
public interface CursorHandler<T extends GuiEventListener> extends CursorTypeFunction<T> {
    /**
     * Returns the target element associated with this cursor handler.
     * The target element is determined either by the element class or the binary name.
     *
     * @return a {@link TargetElement} containing either the element class or its binary name
     */
    @SuppressWarnings("unchecked")
    default @NotNull TargetElement<T> getTargetElement() {
        TypeToken<T> typeToken = new TypeToken<>(getClass()) {
        };
        return TargetElement.fromClass((Class<T>) typeToken.getRawType());
    }

    /**
     * Represents the target element of the {@link CursorHandler}.
     */
    sealed interface TargetElement<T extends GuiEventListener> permits TargetElement.ClassRef, TargetElement.NameRef {
        /**
         * Creates a {@link TargetElement} from the given element class.
         *
         * @param elementClass the class of the target element
         * @return a {@link TargetElement} containing the element class
         */
        static <T extends GuiEventListener> TargetElement<T> fromClass(Class<T> elementClass) {
            return new ClassRef<>(elementClass);
        }

        /**
         * Creates a {@link TargetElement} from the given binary name.
         *
         * <p>Use the intermediary mappings for native Minecraft elements.</p>
         *
         * @param className the binary name of the target element
         * @return a {@link TargetElement} containing the binary name for reflection
         */
        static <T extends GuiEventListener> TargetElement<T> fromClassName(String className) {
            return new NameRef<>(className);
        }

        /**
         * Represents a target element identified by its {@link Class}.
         *
         * @param elementClass the class of the target element
         */
        record ClassRef<T extends GuiEventListener>(Class<T> elementClass) implements TargetElement<T> {
        }

        /**
         * Represents a target element identified by binary name.
         * <p>
         * The binary name can be used when the target element is inaccessible,
         * allowing for reflection-based access to the class.
         * </p>
         *
         * @param className the binary name of the target element
         */
        record NameRef<T extends GuiEventListener>(String className) implements TargetElement<T> {
        }
    }
}
