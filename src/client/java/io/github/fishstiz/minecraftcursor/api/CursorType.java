package io.github.fishstiz.minecraftcursor.api;

import io.github.fishstiz.minecraftcursor.cursor.CursorTypeRegistry;

/**
 * Represents a cursor type identified by a unique key.
 * <p>
 * The key should correspond to the cursor's file name, its entry in {@code cursors.json},
 * and the suffix of the cursor type translation key.
 * </p>
 */
public interface CursorType {
    /**
     * @return The key that corresponds with the cursor's file name, its entry in {@code cursors.json},
     * and the suffix of the cursor type translation key.
     */
    String getKey();

    /**
     * The fallback cursor type
     */
    CursorType DEFAULT = CursorTypeRegistry.put("default");

    /**
     * The cursor type is determined in two passes:
     * <ol>
     *  <li>The first pass computes the cursor type for the current screen.</li>
     *  <li>The second pass computes the cursor type for the hovered element,
     *      but is skipped if the first pass result is not {@code CursorType.DEFAULT}.</li>
     * </ol>
     * {@code CursorType.DEFAULT_FORCE} ensures the second pass is skipped
     * and forces the cursor to revert to {@code CursorType.DEFAULT} since it is not a real cursor type.
     */
    CursorType DEFAULT_FORCE = () -> "";

    CursorType POINTER = CursorTypeRegistry.put("pointer");

    CursorType GRABBING = CursorTypeRegistry.put("grabbing");

    CursorType TEXT = CursorTypeRegistry.put("text");

    CursorType SHIFT = CursorTypeRegistry.put("shift");
}
