package io.github.fishstiz.minecraftcursor.api;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;

public class CursorController {
    private static CursorController instance;
    private final CursorManager cursorManager;

    private CursorController(CursorManager cursorManager) {
        this.cursorManager = cursorManager;
    }

    public static CursorController getInstance() {
        if (instance == null) {
            instance = new CursorController(MinecraftCursorClient.CURSOR_MANAGER);
        }
        return instance;
    }

    /**
     * Changes the current cursor to the specified type for a single render/tick cycle.
     * After the cycle, the cursor will revert to its default behavior.
     * <ul>
     *  <li>Render is used if there {@code currentScreen} is not {@code null}</li>
     *  <li>Tick is used if {@code currentScreen} is {@code null}, a {@code Screen} is initialized,
     *  and if cursor is not locked</li>
     * </ul>
     *
     * <p><b>Note:</b> This is overridden by the {@link #overrideCursor(CursorType, int)} method.</p>
     *
     * @param cursorType the {@link CursorType} to apply for the current cycle
     */
    public void setSingleCycleCursor(CursorType cursorType) {
        MinecraftCursorClient.setSingleCycleCursor(cursorType);
    }

    /**
     * Override the current cursor with a specified type and index.
     * If multiple overrides exist, the one with the highest index takes precedence.
     *
     * @param cursorType  the {@link CursorType} to override with
     * @param index the index of the cursor override
     */
    public void overrideCursor(CursorType cursorType, int index) {
        cursorManager.overrideCurrentCursor(cursorType, index);
    }

    /**
     * Removes the cursor override at the given index.
     *
     * @param index the index of the override to remove
     */
    public void removeOverride(int index) {
        cursorManager.removeOverride(index);
    }
}
