package io.github.fishstiz.minecraftcursor.api;

/**
 * An alternative to the {@link ElementRegistrar} approach to determine the cursor type directly from the implementing {@link net.minecraft.client.gui.components.events.GuiEventListener}
 * <p>
 * Implement this directly to your element if Minecraft Cursor is a required dependency.
 * </p>
 *
 * <p>
 * <b>Note:</b> The {@link net.minecraft.client.gui.components.events.GuiEventListener} must either be the current screen or be accessible from the current screen or
 * from its parent element through {@link net.minecraft.client.gui.components.events.ContainerEventHandler#children()}.
 * </p>
 */
public interface CursorProvider {
    /**
     * Returns the cursor type to be applied when the mouse is over the element.
     * <p>
     * This method is invoked after screen render and when
     * {@link net.minecraft.client.gui.components.events.GuiEventListener#isMouseOver(double mouseX, double mouseY)}
     * returns {@code true}
     * </p>
     *
     * @param mouseX the X coordinate of the mouse
     * @param mouseY the Y coordinate of the mouse
     * @return The {@link CursorType} to be applied.
     */
    CursorType getCursorType(double mouseX, double mouseY);
}
