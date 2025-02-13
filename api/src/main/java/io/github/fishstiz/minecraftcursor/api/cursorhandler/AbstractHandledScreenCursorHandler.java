package io.github.fishstiz.minecraftcursor.api.cursorhandler;

import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.impl.HandleScreenCursorProvider;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;


/**
 * An abstract class for handling cursor types in {@link HandledScreen} elements.
 *
 * <p>Extend this class to help retain the behavior of the cursor type in the base {@link HandledScreen}</p>
 *
 * @param <T> The type of {@link ScreenHandler} associated with the {@link HandledScreen} to be registered.
 * @param <U> The type of {@link HandledScreen} itself.
 */
public abstract class AbstractHandledScreenCursorHandler<T extends ScreenHandler, U extends HandledScreen<? extends T>> implements CursorHandler<U> {
    /**
     * Returns the computed {@link CursorType} of the base {@link HandledScreen}.
     *
     * <p>Override this method in a subclass to implement custom logic for calculating the cursor type of
     * the {@link HandledScreen} element.</p>
     *
     * <p>After evaluating the cursor type in the subclass, you can invoke and return
     * {@code super.getCursorType(handledScreen, mouseX, mouseY} to return the cursor type of the base {@link HandledScreen}</p>
     *
     * @param handledScreen The {@link HandledScreen} element.
     * @param mouseX        The X-coordinate of the mouse cursor.
     * @param mouseY        The Y-coordinate of the mouse cursor.
     * @return The computed cursor type of the base {@link HandledScreen}.
     */
    @Override
    public CursorType getCursorType(U handledScreen, double mouseX, double mouseY) {
        return HandleScreenCursorProvider.getCursorType();
    }
}
