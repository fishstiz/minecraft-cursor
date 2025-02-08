package io.github.fishstiz.minecraftcursor.api;

/**
 * Used for registering {@link CursorType} objects to load its resources.
 */
public interface CursorTypeRegistrar {
    /**
     * Registers a {@link CursorType} object to be loaded.
     *
     * @param cursorType The cursor type to be registered.
     */
    void register(CursorType cursorType);
}
