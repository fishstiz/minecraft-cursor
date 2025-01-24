package io.github.fishstiz.minecraftcursor.mixin.client;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor<T extends ScreenHandler> {
    @Accessor("backgroundWidth")
    int getBackgroundWidth();

    @Accessor("backgroundHeight")
    int getBackgroundHeight();

    @Accessor("handler")
    T getHandler();

    @Accessor("focusedSlot")
    Slot getFocusedSlot();

    @Accessor("x")
    int getX();

    @Accessor("y")
    int getY();

    @Invoker("isPointWithinBounds")
    boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY);
}
