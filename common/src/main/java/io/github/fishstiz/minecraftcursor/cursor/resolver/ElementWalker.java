package io.github.fishstiz.minecraftcursor.cursor.resolver;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

public final class ElementWalker {
    private ElementWalker() {
    }

    public static <T> T walk(
            ContainerEventHandler parent,
            double mouseX,
            double mouseY,
            Processor<T> processor,
            Predicate<T> shouldReturn,
            T defaultValue
    ) {
        if (containsPoint(parent, mouseX, mouseY)) {
            for (GuiEventListener child : parent.children()) {
                if (child instanceof ContainerEventHandler nestedParent) {
                    T result = walk(nestedParent, mouseX, mouseY, processor, shouldReturn, defaultValue);
                    if (shouldReturn.test(result)) {
                        return result;
                    }
                }
                if (containsPoint(child, mouseX, mouseY)) {
                    T result = processor.processNode(child, mouseX, mouseY);
                    if (shouldReturn.test(result)) {
                        return result;
                    }
                }
            }
        }
        return defaultValue;
    }

    public static @Nullable GuiEventListener findDeepest(ContainerEventHandler parent, double mouseX, double mouseY) {
        return ElementWalker.walk(parent, mouseX, mouseY, (child, x, y) -> child, Objects::nonNull, null);
    }

    /**
     * Include inactive widgets.
     *
     * <p>
     * Default implementation of {@link AbstractWidget#isMouseOver(double, double)}
     * returns false if {@link AbstractWidget#active} is {@code false}.
     */
    private static boolean containsPoint(GuiEventListener guiEventListener, double mouseX, double mouseY) {
        if (guiEventListener instanceof AbstractWidget widget) {
            return widget.visible && (widget.isHovered() || widget.isMouseOver(mouseX, mouseY));
        }
        return guiEventListener.isMouseOver(mouseX, mouseY);
    }

    @FunctionalInterface
    public interface Processor<T> {
        T processNode(GuiEventListener node, double mouseX, double mouseY);
    }
}
