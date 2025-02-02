package io.github.fishstiz.minecraftcursor.api;

import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;

/**
 * The implementing class must be an entry point of {@code "minecraft-cursor"} in {@code fabric.mod.json}
 * <p><strong>Example:</strong></p>
 * <pre>
 * "entrypoints" {
 *   "minecraft-cursor": ["com.example.modid.MinecraftCursorApiImpl"]
 * }
 * </pre>
 */
public interface MinecraftCursorApi {
    /**
     * Allows registering {@link net.minecraft.client.gui.Element} classes with a cursor type function:
     *
     * <p><strong>Example usage:</strong></p>
     * <pre>{@code
     * public void init(CursorTypeRegistry cursorTypeRegistry) {
     *      cursorTypeRegistry.register(MyButton.class, (myButton, mouseX, mouseY) -> CursorType.POINTER);
     *      // you can use the existing static methods in CursorTypeRegistry
     *      cursorTypeRegistry.register(MyOtherButton.class, CursorTypeRegistry::elementToPointer);
     * }
     * }</pre>
     *
     * @param cursorTypeRegistry the registry used to associate element classes with cursor types
     *
     * @apiNote The {@code Element} must either be the current screen or be accessible through
     * {@link net.minecraft.client.gui.ParentElement#children()} from the current screen or from its parent element.
     * <br>
     * Any containers or nested parent elements must be an instance of {@link net.minecraft.client.gui.ParentElement}
     * and also be accessible through {@link net.minecraft.client.gui.ParentElement#children()} from the current screen
     * to the  parent element.
     */
    void init(CursorTypeRegistry cursorTypeRegistry);
}
