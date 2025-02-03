package io.github.fishstiz.minecraftcursor.api;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
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
     * Initialize your cursors and register your elements here:
     *
     * <p><strong>Example usage:</strong></p>
     * <pre>{@code
     * public void init(CursorTypeFactory cursorTypeFactory, CursorTypeRegistry cursorTypeRegistry) {
     *      CursorType customCursorType = cursorTypeFactory.create("custom-cursor");
     *
     *      // Registering my button class with the pointer cursor
     *      cursorTypeRegistry.register(MyButton.class, CursorTypeRegistry::elementToPointer);
     *      // Registering my custom element with my custom cursor
     *      cursorTypeRegistry.register(MyCustomElement.class, (myCustomElement, mouseX, mouseY) -> customCursorType);
     * }
     * }</pre>
     *
     * @param cursorTypeFactory  the functional interface for creating {@link CursorType} objects based on a given key.
     * @param cursorTypeRegistry the registry used to associate element classes with cursor types
     */
    void init(CursorTypeFactory cursorTypeFactory, CursorTypeRegistry cursorTypeRegistry);
}
