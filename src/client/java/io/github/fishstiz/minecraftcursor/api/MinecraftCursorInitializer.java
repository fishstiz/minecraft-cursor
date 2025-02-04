package io.github.fishstiz.minecraftcursor.api;

/**
 * The entrypoint for Minecraft Cursor on {@link net.fabricmc.api.EnvType#CLIENT} initialization.
 *
 * <p>This is where you can initialize your cursor types and register elements.</p>
 *
 * <p>In {@code fabric.mod.json}, the entrypoint is defined with {@code minecraft-cursor} key.</p>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 * "entrypoints" {
 *   "minecraft-cursor": ["com.example.modid.MinecraftCursorApiImpl"]
 * }
 * </pre>
 */
public interface MinecraftCursorInitializer {
    /**
     * The {@link CursorTypeFactory} and {@link CursorTypeRegistrar} are injected here
     * for initializing your cursor types and registering your elements.
     *
     * <p><strong>Example usage:</strong></p>
     * <pre>{@code
     * public void init(CursorTypeFactory cursorTypeFactory, CursorTypeRegistrar cursorTypeRegistrar) {
     *      CursorType customCursorType = cursorTypeFactory.of("custom-cursor");
     *
     *      // Registering my button class with the pointer cursor
     *      cursorTypeRegistrar.register(MyButton.class, CursorTypeRegistrar::elementToPointer);
     *      // Registering my custom element with my custom cursor
     *      cursorTypeRegistrar.register(MyCustomElement.class, (myCustomElement, mouseX, mouseY) -> customCursorType);
     * }
     * }</pre>
     *
     * @param cursorTypeFactory  the functional interface for creating {@link CursorType} objects based on a given key.
     * @param cursorTypeRegistrar the registrar used to associate element classes with cursor types
     */
    void init(CursorTypeFactory cursorTypeFactory, CursorTypeRegistrar cursorTypeRegistrar);
}
