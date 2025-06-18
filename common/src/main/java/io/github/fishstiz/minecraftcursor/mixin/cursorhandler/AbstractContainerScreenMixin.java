package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.cursor.handler.InternalCursorProvider;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements InternalCursorProvider {
    @Shadow @Final protected T menu;

    @Shadow @Nullable protected Slot hoveredSlot;

    @Shadow protected int imageHeight;

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

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
