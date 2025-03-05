package io.github.fishstiz.minecraftcursor.api;

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
     * Binds a {@link CursorType} object to a key that serves as its identifier.
     * <p>
     * You must register your {@link CursorType} in
     * {@link MinecraftCursorInitializer#init(CursorTypeRegistrar, ElementRegistrar)}
     * so its resource is loaded when Minecraft first loads.
     *
     * @param key The identifier of the cursor type
     * @return {@link CursorType}
     */
    static CursorType of(String key) {
        return () -> key;
    }

    /**
     * The fallback cursor type
     */
    CursorType DEFAULT = CursorType.of("default");

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
    CursorType DEFAULT_FORCE = CursorType.of("");

    /**
     * The cursor type that is applied when the mouse is over {@link net.minecraft.client.gui.widget.PressableWidget}
     * elements and on other certain elements that can be clicked.
     */
    CursorType POINTER = CursorType.of("pointer");

    /**
     * The cursor type that is applied when grabbing the slider of {@link net.minecraft.client.gui.widget.SliderWidget}
     * elements and when grabbing items in the inventory.
     */
    CursorType GRABBING = CursorType.of("grabbing");

    /**
     * The cursor type that is applied when the mouse is over {@link net.minecraft.client.gui.widget.TextFieldWidget} elements
     */
    CursorType TEXT = CursorType.of("text");

    /**
     * The cursor type that is applied when shift is held and the mouse is over elements with special shift actions.
     */
    CursorType SHIFT = CursorType.of("shift");

    /**
     * The cursor type that is applied when loading.
     */
    CursorType BUSY = CursorType.of("busy");

    /**
     * Replaces {@code GLFW_CROSSHAIR_CURSOR} standard cursor.
     */
    CursorType CROSSHAIR = CursorType.of("crosshair");

    /**
     * Replaces {@code GLFW_RESIZE_EW_CURSOR} standard cursor.
     */
    CursorType RESIZE_EW = CursorType.of("resize_ew");

    /**
     * Replaces {@code GLFW_RESIZE_NS_CURSOR} standard cursor.
     */
    CursorType RESIZE_NS = CursorType.of("resize_ns");

    /**
     * Replaces {@code GLFW_RESIZE_NWSE_CURSOR} standard cursor.
     */
    CursorType RESIZE_NWSE = CursorType.of("resize_nwse");

    /**
     * Replaces {@code GLFW_RESIZE_NESW_CURSOR} standard cursor.
     */
    CursorType RESIZE_NESW = CursorType.of("resize_nesw");

    /**
     * Replaces {@code GLFW_NOT_ALLOWED_CURSOR} standard cursor.
     */
    CursorType NOT_ALLOWED = CursorType.of("not_allowed");
}
