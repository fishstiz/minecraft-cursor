package io.github.fishstiz.minecraftcursor.api;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;

/**
 * The registrar used to register element classes with a function that determines their cursor type
 */
public interface CursorTypeRegistrar {
    /**
     * Registers an {@link Element} class using the {@link CursorHandler#getTargetElement()} method
     * with the method {@link CursorHandler#getCursorType(Element, double, double)} to determine its {@link CursorType}.
     * <p>
     * The {@link CursorHandler} provides logic for determining the cursor type based on the element and mouse position.
     * The target element can be identified either by its class or by its fully qualified class name (FQCN) for cases
     * where direct access to the class is not possible.
     * </p>
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
     * @param cursorHandler The {@link CursorHandler} implementation that defines how the cursor should behave for a specific element.
     *
     * @throws AssertionError if the {@link CursorHandler} does not provide either an element class or an FQCN.
     */
    <T extends Element> void register(CursorHandler<T> cursorHandler);

    /**
     * Registers an {@link Element} class specified by the FQCN using reflection
     * with a function that determines its {@link CursorType}.
     *
     * <p><strong>Example usage:</strong></p>
     * <pre>
     * {@code register("net.minecraft.class_4264", (pressableWidget, mouseX, mouseY) -> CursorType.POINTER); }
     * </pre>
     *
     * @param fullyQualifiedClassName The fully qualified class name of the {@link Element} to register.
     *                                Use the intermediary name when registering native Minecraft elements.
     * @param elementToCursorType A function that takes an instance of the element, mouse X, and mouse Y positions,
     *                            and returns the corresponding {@link CursorType}.
     *
     * @apiNote The {@link Element} must either be the current screen or be accessible from the current screen or
     * from its parent element through {@link ParentElement#children()}.
     * <br>
     * Any containers or nested parent elements must be an instance of {@link ParentElement} and also be accessible
     * through {@link ParentElement#children()} from the current screen to the parent element.
     */
    <T extends Element> void register(String fullyQualifiedClassName, ElementCursorTypeFunction<T> elementToCursorType);

    /**
     * Registers an {@link Element} class with a function that determines its {@link CursorType}.
     *
     * <p><strong>Example usage:</strong></p>
     * <pre>{@code
     *      register(MyButton.class, (myButton, mouseX, mouseY) -> CursorType.POINTER);
     *      // you can use the existing static methods in CursorTypeRegistrar
     *      register(MyOtherButton.class, CursorTypeRegistrar::elementToPointer);
     * }</pre>
     *
     * @param elementClass        The {@link Class} of the {@link Element} to register
     * @param elementToCursorType A function that takes an instance of the element, mouse X, and mouse Y positions,
     *                            and returns the corresponding {@link CursorType}.
     * @apiNote The {@link Element} must either be the current screen or be accessible from the current screen or
     * from its parent element through {@link ParentElement#children()}.
     * <br>
     * Any containers or nested parent elements must be an instance of {@link ParentElement} and also be accessible
     * through {@link ParentElement#children()} from the current screen to the parent element.
     */
    <T extends Element> void register(Class<T> elementClass, ElementCursorTypeFunction<T> elementToCursorType);

    /**
     * Returns the default {@link CursorType} for the given element.
     * <p>
     * Use this method when no additional logic is needed to determine the cursor type for the element.
     * </p>
     *
     * @param ignoreElement The {@link Element} for which the cursor type is being determined.
     *                      This parameter is ignored as the default cursor is always returned.
     * @param ignoreMouseX The mouse X-coordinate. This parameter is ignored in this method.
     * @param ignoreMouseY The mouse Y-coordinate. This parameter is ignored in this method.
     * @return The default {@link CursorType} (i.e., {@link CursorType#DEFAULT}).
     */
    static CursorType elementToDefault(Element ignoreElement, double ignoreMouseX, double ignoreMouseY) {
        return CursorType.DEFAULT;
    }

    /**
     * Returns the pointer {@link CursorType} for the given element.
     * <p>
     * Use this method when no additional logic is needed for the {@link CursorType#POINTER} element.
     * </p>
     *
     * @param ignoreElement The {@link Element} for which the cursor type is being determined.
     *                      This parameter is ignored as the pointer cursor is always returned.
     * @param ignoreMouseX The mouse X-coordinate. This parameter is ignored in this method.
     * @param ignoreMouseY The mouse Y-coordinate. This parameter is ignored in this method.
     * @return The pointer {@link CursorType} (i.e., {@link CursorType#POINTER}).
     */
    static CursorType elementToPointer(Element ignoreElement, double ignoreMouseX, double ignoreMouseY) {
        return CursorType.POINTER;
    }

    /**
     * Returns the text {@link CursorType} for the given element.
     * <p>
     * Use this method when no additional logic is needed for the {@link CursorType#TEXT} element.
     * </p>
     *
     * @param ignoreElement The {@link Element} for which the cursor type is being determined.
     *                      This parameter is ignored as the text cursor is always returned.
     * @param ignoreMouseX The mouse X-coordinate. This parameter is ignored in this method.
     * @param ignoreMouseY The mouse Y-coordinate. This parameter is ignored in this method.
     * @return The text {@link CursorType} (i.e., {@link CursorType#TEXT}).
     */
    static CursorType elementToText(Element ignoreElement, double ignoreMouseX, double ignoreMouseY) {
        return CursorType.TEXT;
    }

    /**
     * A functional interface that defines a method for determining the {@link CursorType} for the given
     * {@link Element} when moused over.
     *
     * @param <T> The type of the {@link Element} for which the cursor type is being determined.
     */
    @FunctionalInterface
    interface ElementCursorTypeFunction<T extends Element> {
        /**
         * @param element The {@link Element} for which the cursor type is being determined.
         * @param mouseX The mouse X-coordinate.
         * @param mouseY The mouse Y-coordinate.
         * @return The {@link CursorType} to be applied for the element when moused over.
         */
        CursorType getCursorType(T element, double mouseX, double mouseY);
    }
}
