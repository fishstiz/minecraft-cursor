package io.github.fishstiz.minecraftcursor.api;

/**
 * The entrypoint for Minecraft Cursor on initialization.
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
     * The {@link CursorTypeRegistrar} and {@link ElementRegistrar} instances are injected here
     * where you can register custom cursor types and elements.
     *
     * @param cursorRegistrar  the registrar used to register {@link CursorType} objects.
     * @param elementRegistrar the registrar used to associate element classes with cursor type functions.
     */
    void init(CursorTypeRegistrar cursorRegistrar, ElementRegistrar elementRegistrar);
}
