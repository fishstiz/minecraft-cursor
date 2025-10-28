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
        if (hovered(parent, mouseX, mouseY)) {
            for (GuiEventListener child : parent.children()) {
                if (child instanceof ContainerEventHandler nestedParent) {
                    T result = walk(nestedParent, mouseX, mouseY, processor, shouldReturn, defaultValue);
                    if (shouldReturn.test(result)) {
                        return result;
                    }
                }
                if (hovered(child, mouseX, mouseY)) {
                    return processor.processNode(child, mouseX, mouseY);
                }
            }
        }
        return defaultValue;
    }

    public static @Nullable GuiEventListener findDeepest(ContainerEventHandler parent, double mouseX, double mouseY) {
        return ElementWalker.walk(parent, mouseX, mouseY, (child, x, y) -> child, Objects::nonNull, null);
    }

    public static @Nullable GuiEventListener findFirst(ContainerEventHandler parent, double mouseX, double mouseY) {
        if (hovered(parent, mouseX, mouseY)) {
            for (GuiEventListener child : parent.children()) {
                if (hovered(child, mouseX, mouseY)) {
                    return child;
                }
            }
        }
        return null;
    }

    /**
     * Include inactive widgets.
     *
     * <p>
     * Default implementation of {@link AbstractWidget#isMouseOver(double, double)}
     * returns false if {@link AbstractWidget#active} is {@code false}.
     */
    private static boolean hovered(GuiEventListener guiEventListener, double mouseX, double mouseY) {
        return guiEventListener instanceof AbstractWidget widget
                ? widgetHovered(widget, mouseX, mouseY)
                : guiEventListener.isMouseOver(mouseX, mouseY);
    }

    private static boolean widgetHovered(AbstractWidget widget, double mouseX, double mouseY) {
        return widget.visible &&
               widget.isHovered() &&
               mouseX >= widget.getX() &&
               mouseY >= widget.getY() &&
               mouseX < (widget.getX() + widget.getWidth()) &&
               mouseY < (widget.getY() + widget.getHeight());

    }

    @FunctionalInterface
    public interface Processor<T> {
        T processNode(GuiEventListener node, double mouseX, double mouseY);
    }
}
