package io.github.fishstiz.minecraftcursor.mixin.cursorprovider;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.cursor.InternalCursorProvider;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin implements InternalCursorProvider {
    @Shadow @Final protected AbstractContainerMenu menu;

    @Shadow @Nullable protected Slot hoveredSlot;

    @Override
    public @NotNull CursorType minecraft_cursor$getCursorType(double mouseX, double mouseY) {
        boolean canClickFocusedSlot = menu.getCarried().isEmpty()
                                      && hoveredSlot != null
                                      && hoveredSlot.hasItem()
                                      && hoveredSlot.isHighlightable();

        if (canClickFocusedSlot && CursorTypeUtil.canShift()) {
            return CursorType.SHIFT;
        }
        if (CONFIG.isItemSlotEnabled() && canClickFocusedSlot) {
            return CursorType.POINTER;
        }
        if (CONFIG.isItemGrabbingEnabled() && !menu.getCarried().isEmpty()) {
            return CursorType.GRABBING;
        }
        return CursorType.DEFAULT;
    }
}
