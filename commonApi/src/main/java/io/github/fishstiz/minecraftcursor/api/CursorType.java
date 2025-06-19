package io.github.fishstiz.minecraftcursor.api;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.EditBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

/**
 * Represents a cursor type identified by a unique key.
 * <p>
 * The key should correspond to the cursor's file name, its entry in {@code cursors.json},
 * and the suffix of the cursor type translation key.
 * </p>
 */
public interface CursorType {
    /**
     * The fallback cursor type
     * <p>
     * Maps to {@link GLFW#GLFW_ARROW_CURSOR} standard cursor.
     */
    CursorType DEFAULT = of("default");

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
    CursorType DEFAULT_FORCE = of("");

    /**
     * The cursor type that is applied when the mouse is over {@link AbstractButton}
     * elements and on other certain elements that can be clicked.
     *
     * <p>
     * Maps to {@link GLFW#GLFW_POINTING_HAND_CURSOR} standard cursor.
     */
    CursorType POINTER = of("pointer");

    /**
     * The cursor type that is applied when grabbing the slider of {@link AbstractSliderButton}
     * elements and when grabbing items in the inventory.
     *
     * <p>
     * Maps to {@link GLFW#GLFW_RESIZE_ALL_CURSOR} standard cursor.
     */
    CursorType GRABBING = of("grabbing");

    /**
     * The cursor type that is applied when the mouse is over {@link EditBox} elements
     *
     * <p>
     * Maps to {@link GLFW#GLFW_IBEAM_CURSOR} standard cursor.
     */
    CursorType TEXT = of("text");

    /**
     * The cursor type that is applied when shift is held and the mouse is over elements with special shift actions.
     */
    CursorType SHIFT = of("shift");

    /**
     * The cursor type that is applied when loading.
     */
    CursorType BUSY = of("busy");

    /**
     * Maps to {@link GLFW#GLFW_CROSSHAIR_CURSOR} standard cursor.
     */
    CursorType CROSSHAIR = of("crosshair");

    /**
     * Maps to {@link GLFW#GLFW_RESIZE_EW_CURSOR} standard cursor.
     */
    CursorType RESIZE_EW = of("resize_ew");

    /**
     * Maps to {@link GLFW#GLFW_RESIZE_NS_CURSOR} standard cursor.
     */
    CursorType RESIZE_NS = of("resize_ns");

    /**
     * Maps to {@link GLFW#GLFW_RESIZE_NWSE_CURSOR} standard cursor.
     */
    CursorType RESIZE_NWSE = of("resize_nwse");

    /**
     * Maps to {@link GLFW#GLFW_RESIZE_NESW_CURSOR} standard cursor.
     */
    CursorType RESIZE_NESW = of("resize_nesw");

    /**
     * The cursor type that is applied when the mouse is over disabled
     * {@link AbstractButton} and {@link AbstractSliderButton} widgets.
     *
     * <p>
     * Maps to {@link GLFW#GLFW_NOT_ALLOWED_CURSOR} standard cursor.
     */
    CursorType NOT_ALLOWED = of("not_allowed");

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
        return new CursorType() {
            @Override
            public String getKey() {
                return key;
            }

            @Override
            public String toString() {
                return "CursorType{key='" + key + "'}";
            }
        };
    }

    /**
     * Returns the first non-default {@link CursorType} from the given list.
     * If all are null or default, returns {@link CursorType#DEFAULT}.
     *
     * @param types vararg list of cursor types to evaluate
     * @return the first non-default cursor type, or {@code CursorType.DEFAULT} if none found
     */
    static @NotNull CursorType firstNonDefault(@Nullable CursorType... types) {
        if (types != null) {
            for (CursorType type : types) {
                if (type != null && !type.isDefault()) {
                    return type;
                }
            }
        }
        return CursorType.DEFAULT;
    }

    default boolean isKey(@Nullable CursorType cursorType) {
        return cursorType != null && Objects.equals(this.getKey(), cursorType.getKey());
    }

    default boolean isDefault() {
        return this == CursorType.DEFAULT;
    }
}
