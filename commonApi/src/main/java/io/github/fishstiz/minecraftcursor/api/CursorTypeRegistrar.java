package io.github.fishstiz.minecraftcursor.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * Used for registering {@link CursorType} objects to load its resources.
 */
@ApiStatus.NonExtendable
public interface CursorTypeRegistrar {
    /**
     * Registers a variable number of {@link CursorType} objects to be loaded.
     *
     * @param cursorTypes variable number of {@link CursorType} objects.
     */
    void register(CursorType... cursorTypes);

    /**
     * Creates and registers a {@link CursorType} object using its key.
     *
     * @param key The identifier of the {@link CursorType} to be loaded.
     * @return {@link CursorType}
     */
    CursorType register(String key);
}
