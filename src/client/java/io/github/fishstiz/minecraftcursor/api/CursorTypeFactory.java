package io.github.fishstiz.minecraftcursor.api;

/**
 * Functional interface for creating a {@link CursorType} based on a key.
 * <p>
 * The key should correspond to the cursor's file name, its entry in {@code cursors.json},
 * and the suffix of the cursor type translation key.
 * </p>
 */
@FunctionalInterface
public interface CursorTypeFactory {
    /**
     * @param key The key representing the cursor type.
     *            It should correspond to the cursor type's file name, its entry in {@code cursors.json},
     *            and the suffix of the cursor type translation key.
     * @return A new {@link CursorType} object
     */
    CursorType of(String key);
}
