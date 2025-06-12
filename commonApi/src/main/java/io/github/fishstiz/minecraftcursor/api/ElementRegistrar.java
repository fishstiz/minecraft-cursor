package io.github.fishstiz.minecraftcursor.api;

import net.minecraft.client.gui.components.events.GuiEventListener;

/**
 * The registrar used to map {@link GuiEventListener}s with a {@link CursorTypeFunction}.
 */
public interface ElementRegistrar {
    /**
     * Registers the {@link GuiEventListener} class specified by {@link CursorHandler#getTargetElement()}
     * with the {@link CursorHandler#getCursorType(GuiEventListener, double, double)} callback function.
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
     *                      and the {@link CursorTypeFunction}.
     */
    <T extends GuiEventListener> void register(CursorHandler<T> cursorHandler);

    /**
     * Registers the {@link GuiEventListener} class inferred from the binary name with a {@link CursorTypeFunction}.
     *
     * <p>Fabric note: Use the intermediary mappings when registering a native {@link GuiEventListener}.</p>
     *
     * <p><strong>Example usage:</strong></p>
     * <pre>
     * {@code register("net.minecraft.class_4264", (pressableWidget, mouseX, mouseY) -> CursorType.POINTER); }
     * </pre>
     *
     * @param <T>                The type of the {@link GuiEventListener} to register
     * @param className          The binary name of the {@link GuiEventListener} to register.
     *                           Use the intermediary name when registering a native {@link GuiEventListener}.
     * @param cursorTypeFunction A function that takes an instance of the {@link GuiEventListener}, mouse X, and mouse Y positions,
     *                           and returns the corresponding {@link CursorType}.
     */
    <T extends GuiEventListener> void register(String className, CursorTypeFunction<T> cursorTypeFunction);

    /**
     * Registers the {@link GuiEventListener} class with a {@link CursorTypeFunction} that determines its {@link CursorType}.
     *
     * <p><strong>Example usage:</strong></p>
     * <pre>{@code
     *      register(MyButton.class, (myButton, mouseX, mouseY) -> CursorType.POINTER);
     *      // you can use the existing static methods in ElementRegistrar
     *      register(MyOtherButton.class, ElementRegistrar::elementToPointer);
     * }</pre>
     *
     * @param <T>                The type of the {@link GuiEventListener} to register
     * @param elementClass       The {@link Class} of the {@link GuiEventListener} to register
     * @param cursorTypeFunction A function that takes an instance of the element, mouse X, and mouse Y positions,
     *                           and returns the corresponding {@link CursorType}.
     */
    <T extends GuiEventListener> void register(Class<T> elementClass, CursorTypeFunction<T> cursorTypeFunction);

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
