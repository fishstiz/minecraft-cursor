package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreenMixin<CreativeModeInventoryScreen.ItemPickerMenu> {
    @Shadow
    private boolean scrolling;

    @Shadow
    @Nullable
    private Slot destroyItemSlot;

    @Shadow
    private static CreativeModeTab selectedTab;

    protected CreativeModeInventoryScreenMixin(Component title) {
        super(title);
    }

    @Override
    public @NotNull CursorType minecraft_cursor$getCursorType(double mouseX, double mouseY) {
        if (CursorTypeUtil.canShift()
            && this.hoveredSlot != null
            && this.hoveredSlot == this.destroyItemSlot) {
            return CursorType.SHIFT;
        }
        return super.minecraft_cursor$getCursorType(mouseX, mouseY);
    }

    @Inject(method = "renderBg", at = @At("RETURN"))
    private void forceDefaultOnScroll(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        if (selectedTab.canScroll() && this.scrolling && CursorTypeUtil.isLeftClickHeld()) {
            CursorController.getInstance().setSingleCycleCursor(CursorType.DEFAULT_FORCE);
        }
    }

    @Inject(method = "checkTabHovering", at = @At("RETURN"))
    private void setPointerOnHover(GuiGraphics guiGraphics, CreativeModeTab creativeModeTab, int mouseX, int mouseY, CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftCursor.CONFIG.isCreativeTabsEnabled()
            && creativeModeTab != selectedTab
            && cir.getReturnValue()
            && this.minecraft_cursor$getCursorType(mouseX, mouseY).isDefault()) {
            CursorController.getInstance().setSingleCycleCursor(CursorType.POINTER);
        }
    }
}
