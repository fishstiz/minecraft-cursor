package io.github.fishstiz.minecraftcursor.api;

import io.github.fishstiz.minecraftcursor.provider.CursorControllerProvider;
import org.jetbrains.annotations.ApiStatus;

/**
 * Provides methods that allow direct control of the cursor.
 */
@ApiStatus.NonExtendable
public interface CursorController {
    /**
     * Returns the {@link CursorController} instance.
     *
     * @return the {@link CursorController} instance.
     */
    static CursorController getInstance() {
        return CursorControllerProvider.getInstance();
    }

    /**
     * Changes the current cursor to the specified type for a single render/tick cycle.
     *
     * @param cursorType the {@link CursorType} to apply for the current cycle
     */
    void setSingleCycleCursor(CursorType cursorType);

    /**
     * Sets a fallback cursor type to be used for a single render/tick cycle,
     * if a non-default cursor type could not be resolved on the current screen.
     *
     * @param cursorType the {@link CursorType} to use as a fallback for the current cycle
     */
    void setSingleCycleFallbackCursor(CursorType cursorType);

    /**
     * Override the current cursor with a specified type and index.
     * If multiple overrides exist, the one with the highest index takes precedence.
     *
     * @param cursorType the {@link CursorType} to override with
     * @param index      the index of the cursor override
     */
    void overrideCursor(CursorType cursorType, int index);

    /**
     * Removes the cursor override at the given index.
     *
     * @param index the index of the override to remove
     */
    void removeOverride(int index);
}
