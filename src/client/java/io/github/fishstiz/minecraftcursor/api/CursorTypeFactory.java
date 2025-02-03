package io.github.fishstiz.minecraftcursor.api;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;

/**
 * Functional interface for creating {@link CursorType} objects based on a given key.
 * <p>
 * <b>Note:</b> Follow the Resource Pack Support guide as outlined in the README.
 * You do not need to replace the default cursor types, just add your custom cursors there, and add a translation
 * for your cursor with the translation key: {@code "minecraft-cursor.options.cursor-type.<your cursor key here>"}.
 * </p>
 */
@FunctionalInterface
public interface CursorTypeFactory {
    /**
     * Creates a new {@link CursorType} based on the provided key.
     * <p>
     * <b>Note:</b> Follow the Resource Pack Support guide as outlined in the README.
     * You do not need to replace the default cursor types, just add your custom cursors there,
     * and add a translation for your cursor.
     *
     * @param key The key representing the cursor type.
     *            <ul>
     *              <li>This should be the file name of the cursor type.</li>
     *              <li>The key will be appended with the prefix of the cursor type translation key:
     *                  <br>{@code "minecraft-cursor.options.cursor-type.<your key here>"}.
     *              </li>
     *            </ul>
     * @return A new {@link CursorType} object initialized with the provided key.
     */
    CursorType create(String key);
}
