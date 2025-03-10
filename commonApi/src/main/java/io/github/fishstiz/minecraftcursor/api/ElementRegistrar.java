package io.github.fishstiz.minecraftcursor.api;

import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;

/**
 * The registrar used to map {@link GuiEventListener} classes with {@link CursorTypeFunction} methods.
 *
 * <p>
 * <b>Note:</b> The {@link GuiEventListener} must either be the current screen or be accessible from the current screen or
 * from its parent element through {@link ContainerEventHandler#children()}.
 * </p>
 * Any container or nested container must be an instance of {@link ContainerEventHandler}
 * and be accessible via the {@link ContainerEventHandler#children()} method.
 * <p>
 * This accessibility must be maintained throughout the entire element hierarchy, starting from
 * the current screen down to the deepest nested parent element.
 * </p>
 */
public interface ElementRegistrar {
    /**
     * Registers an {@link GuiEventListener} class as specified by the {@link CursorHandler#getTargetElement()} method
     * with the {@link CursorHandler#getCursorType(GuiEventListener, double, double)} method to determine its {@link CursorType}.
     *
     * <p><strong>Example usage:</strong></p>
     * <pre>{@code
     * register(new CursorHandler<MyButton>() {
     *     @Override
     *     public CursorType getCursorType(MyButton myButton, double mouseX, double mouseY) {
     *         return CursorType.POINTER;
     *     }
     * });
     * }</pre>
     *
     * @param <T>           The type of the {@link GuiEventListener} to register
     * @param cursorHandler The {@link CursorHandler} implementation that provides the target {@link GuiEventListener}
     *                      and the {@link CursorTypeFunction} method.
     */
    <T extends GuiEventListener> void register(CursorHandler<T> cursorHandler);

    /**
     * Registers an {@link GuiEventListener} class specified by the fully qualified class name (FQCN) {@link String}
     * with a function that determines its {@link CursorType}.
     *
     * <p>Use the intermediary mappings when registering native Minecraft elements.</p>
     *
     * <p><strong>Example usage:</strong></p>
     * <pre>
     * {@code register("net.minecraft.class_4264", (pressableWidget, mouseX, mouseY) -> CursorType.POINTER); }
     * </pre>
     *
     * @param <T>                     The type of the {@link GuiEventListener} to register
     * @param fullyQualifiedClassName The fully qualified class name of the {@link GuiEventListener} to register.
     *                                Use the intermediary name when registering native Minecraft elements.
     * @param elementToCursorType     A function that takes an instance of the element, mouse X, and mouse Y positions,
     *                                and returns the corresponding {@link CursorType}.
     */
    <T extends GuiEventListener> void register(String fullyQualifiedClassName, CursorTypeFunction<T> elementToCursorType);

    /**
     * Registers an {@link GuiEventListener} class with a function that determines its {@link CursorType}.
     *
     * <p><strong>Example usage:</strong></p>
     * <pre>{@code
     *      register(MyButton.class, (myButton, mouseX, mouseY) -> CursorType.POINTER);
     *      // you can use the existing static methods in ElementRegistrar
     *      register(MyOtherButton.class, ElementRegistrar::elementToPointer);
     * }</pre>
     *
     * @param <T>                 The type of the {@link GuiEventListener} to register
     * @param elementClass        The {@link Class} of the {@link GuiEventListener} to register
     * @param elementToCursorType A function that takes an instance of the element, mouse X, and mouse Y positions,
     *                            and returns the corresponding {@link CursorType}.
     */
    <T extends GuiEventListener> void register(Class<T> elementClass, CursorTypeFunction<T> elementToCursorType);

    /**
     * A built-in {@link CursorTypeFunction} static method that always returns {@link CursorType#DEFAULT}.
     * <p>
     * Use this static method when no additional logic is needed to determine the cursor type for the element.
     * </p>
     *
     * @param ignoreElement The {@link GuiEventListener} for which the cursor type is being determined.
     *                      This parameter is ignored as the default cursor is always returned.
     * @param ignoreMouseX  The mouse X-coordinate. This parameter is ignored in this method.
     * @param ignoreMouseY  The mouse Y-coordinate. This parameter is ignored in this method.
     * @return {@link CursorType#DEFAULT}
     */
    static CursorType elementToDefault(GuiEventListener ignoreElement, double ignoreMouseX, double ignoreMouseY) {
        return CursorType.DEFAULT;
    }

    /**
     * A built-in {@link CursorTypeFunction} static method that always returns {@link CursorType#POINTER}.
     * <p>
     * Use this static method when no additional logic is needed for the {@link CursorType#POINTER} element.
     * </p>
     *
     * @param ignoreElement The {@link GuiEventListener} for which the cursor type is being determined.
     *                      This parameter is ignored as the pointer cursor is always returned.
     * @param ignoreMouseX  The mouse X-coordinate. This parameter is ignored in this method.
     * @param ignoreMouseY  The mouse Y-coordinate. This parameter is ignored in this method.
     * @return {@link CursorType#POINTER}
     */
    static CursorType elementToPointer(GuiEventListener ignoreElement, double ignoreMouseX, double ignoreMouseY) {
        return CursorType.POINTER;
    }

    /**
     * A built-in {@link CursorTypeFunction} static method that always returns {@link CursorType#TEXT}.
     * <p>
     * Use this static method when no additional logic is needed for the {@link CursorType#TEXT} element.
     * </p>
     *
     * @param ignoreElement The {@link GuiEventListener} for which the cursor type is being determined.
     *                      This parameter is ignored as the text cursor is always returned.
     * @param ignoreMouseX  The mouse X-coordinate. This parameter is ignored in this method.
     * @param ignoreMouseY  The mouse Y-coordinate. This parameter is ignored in this method.
     * @return {@link CursorType#TEXT}
     */
    static CursorType elementToText(GuiEventListener ignoreElement, double ignoreMouseX, double ignoreMouseY) {
        return CursorType.TEXT;
    }

    /**
     * A functional interface that defines a method for determining the {@link CursorType} for the given
     * {@link GuiEventListener} when moused over.
     *
     * @param <T> The type of the {@link GuiEventListener} to register
     */
    @FunctionalInterface
    interface CursorTypeFunction<T extends GuiEventListener> {
        /**
         * Determines the cursor type based on the {@link GuiEventListener}, and the X and Y coordinates of the mouse.
         *
         * @param element The {@link GuiEventListener} that is registered
         * @param mouseX  The mouse X-coordinate.
         * @param mouseY  The mouse Y-coordinate.
         * @return The {@link CursorType} to be applied for the element when moused over.
         */
        CursorType getCursorType(T element, double mouseX, double mouseY);
    }
}
