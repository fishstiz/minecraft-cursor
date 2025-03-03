package io.github.fishstiz.minecraftcursor.mixin.access;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerScreen.class)
public interface HandledScreenAccessor<T extends AbstractContainerMenu> {
    @Accessor("imageWidth")
    int getBackgroundWidth();

    @Accessor("imageHeight")
    int getBackgroundHeight();

    @Accessor("menu")
    T getHandler();

    @Accessor("hoveredSlot")
    Slot getFocusedSlot();

    @Accessor("leftPos")
    int getX();

    @Accessor("topPos")
    int getY();

    @Invoker("isHovering")
    boolean invokeIsPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY);
}
