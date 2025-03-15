package io.github.fishstiz.minecraftcursor.api;

/**
 * The entrypoint for Minecraft Cursor on initialization.
 *
 * <p>This is where you can initialize your cursor types and register elements.</p>
 *
 * <p><b>Fabric</b>: Register your entrypoint in {@code fabric.mod.json} under the {@code minecraft-cursor} key.</p>
 * <p><b>Forge</b> & <b>NeoForge</b>: Register your entrypoint as a service using the {@code ServiceLoader} pattern.</p>
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
