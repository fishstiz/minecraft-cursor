package io.github.fishstiz.minecraftcursor.api.cursorhandler;

import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.provider.HandledScreenCursorProvider;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;


/**
 * An abstract class for handling cursor types in {@link AbstractContainerScreen} elements.
 *
 * <p>Extend this class to help retain the behavior of the cursor type in the base {@link AbstractContainerScreen}</p>
 *
 * @param <T> The type of {@link AbstractContainerMenu} associated with the {@link AbstractContainerScreen} to be registered.
 * @param <U> The type of {@link AbstractContainerScreen} itself.
 */
public abstract class AbstractHandledScreenCursorHandler<T extends AbstractContainerMenu, U extends AbstractContainerScreen<? extends T>> implements CursorHandler<U> {
    /**
     * Returns the computed {@link CursorType} of the base {@link AbstractContainerScreen}.
     *
     * <p>Override this method in a subclass to implement custom logic for calculating the cursor type of
     * the {@link AbstractContainerScreen} element.</p>
     *
     * <p>After evaluating the cursor type in the subclass, you can invoke and return
     * {@code super.getCursorType(handledScreen, mouseX, mouseY} to return the cursor type of the base {@link AbstractContainerScreen}</p>
     *
     * @param handledScreen The {@link AbstractContainerScreen} element.
     * @param mouseX        The X-coordinate of the mouse cursor.
     * @param mouseY        The Y-coordinate of the mouse cursor.
     * @return The computed cursor type of the base {@link AbstractContainerScreen}.
     */
    @Override
    public CursorType getCursorType(U handledScreen, double mouseX, double mouseY) {
        return HandledScreenCursorProvider.getCursorType();
    }
}
