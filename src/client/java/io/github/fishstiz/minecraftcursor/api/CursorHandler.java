package io.github.fishstiz.minecraftcursor.api;

import com.google.common.reflect.TypeToken;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import net.minecraft.client.gui.Element;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface CursorHandler<T extends Element> {
    @SuppressWarnings("unchecked")
    default @NotNull TargetElement<T> getTargetElement() {
        TypeToken<T> typeToken = new TypeToken<>(getClass()) {
        };
        return TargetElement.fromClass((Class<T>) typeToken.getRawType());
    }

    CursorType getCursorType(T element, double mouseX, double mouseY);

    record TargetElement<T extends Element>(
            Optional<Class<T>> elementClass,
            Optional<String> fullyQualifiedClassName
    ) {
        public static <T extends Element> TargetElement<T> fromClass(Class<T> elementClass) {
            return new TargetElement<>(Optional.of(elementClass), Optional.empty());
        }

        public static <T extends Element> TargetElement<T> fromClassName(String fullyQualifiedClassName) {
            return new TargetElement<>(Optional.empty(), Optional.of(fullyQualifiedClassName));
        }
    }
}
