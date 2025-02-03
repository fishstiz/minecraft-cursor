package io.github.fishstiz.minecraftcursor.api;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;

/**
 * This interface should only be implemented by instances of {@link net.minecraft.client.gui.Element}.
 * <p>
 * The {@code Element} must either be the current screen or be accessible through
 * {@link net.minecraft.client.gui.ParentElement#children()} from the current screen or from its parent element.
 * </p>
 * <p>
 * Any containers or nested parent elements must be an instance of {@link net.minecraft.client.gui.ParentElement}
 * and also be accessible through {@link net.minecraft.client.gui.ParentElement#children()} from the current screen
 * to the parent element.
 * </p>
 * @apiNote An alternative to the {@link CursorTypeRegistrar} approach.
 * <br>
 * <p>
 * Implement this directly to your element if Minecraft Cursor is a required dependency.
 * </p>
 * <br>
 * <p>
 * For optional dependencies, you can create a wrapper element that implements {@code CursorProvider} and use that if the mod is loaded.
 * </p>
 */
public interface CursorProvider {
    /**
     * Retrieves the cursor type to be applied when the mouse is over the element.
     * <p>
     * This method is invoked after screen render and when
     * {@link net.minecraft.client.gui.Element#isMouseOver(double mouseX, double mouseY)}
     * returns {@code true}
     * </p>
     *
     * @param mouseX the X coordinate of the mouse
     * @param mouseY the Y coordinate of the mouse
     * @return The {@link CursorType} to be applied.
     */
    CursorType getCursorType(double mouseX, double mouseY);
}
