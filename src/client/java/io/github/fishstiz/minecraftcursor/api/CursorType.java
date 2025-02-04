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
     * Returns the key of the {@link CursorType} object which acts as its identifier.
     *
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
     *      but is skipped if the first pass result is not {@link CursorType#DEFAULT}.</li>
     * </ol>
     * This cursor type ensures that the second pass is skipped
     * and forces the cursor type to fall back to {@link CursorType#DEFAULT} since it is not a real cursor type.
     */
    CursorType DEFAULT_FORCE = () -> "";

    /**
     * The cursor type that is applied when the mouse is over {@link net.minecraft.client.gui.widget.PressableWidget}
     * elements and on other certain elements that can be clicked.
     */
    CursorType POINTER = CursorTypeRegistry.put("pointer");

    /**
     * The cursor type that is applied when grabbing the slider of {@link net.minecraft.client.gui.widget.SliderWidget}
     * elements and when grabbing items in the inventory.
     */
    CursorType GRABBING = CursorTypeRegistry.put("grabbing");

    /**
     * The cursor type that is applied when the mouse is over {@link net.minecraft.client.gui.widget.TextFieldWidget} elements
     */
    CursorType TEXT = CursorTypeRegistry.put("text");

    /**
     * The cursor type that is applied when shift is held and the mouse is over elements with special shift actions.
     */
    CursorType SHIFT = CursorTypeRegistry.put("shift");
}
