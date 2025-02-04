package io.github.fishstiz.minecraftcursor.api;

/**
 * Functional interface for creating and binding a {@link CursorType} object with a key.
 * <p>
 * The key should correspond to the cursor's file name, its entry in {@code cursors.json},
 * and the suffix of the cursor type translation key.
 * </p>
 */
@FunctionalInterface
public interface CursorTypeFactory {
    /**
     * Creates and binds a new {@link CursorType} object with a key that acts as the identifier.
     *
     * @param key The key identifier of the cursor type.
     *            It should correspond to the cursor type's file name, its entry in {@code cursors.json},
     *            and the suffix of the cursor type translation key.
     * @return A new {@link CursorType} object
     */
    CursorType of(String key);
}
