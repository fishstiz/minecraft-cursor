package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.cursorhandler.access.AdvancementScreenAccess;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementTab.class)
public abstract class AdvancementTabMixin {
    @Shadow
    public abstract AdvancementsScreen getScreen();

    @Inject(method = "isMouseOver", at = @At("RETURN"))
    private void setPointerOnHover(int offsetX, int offsetY, double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftCursor.CONFIG.isAdvancementTabsEnabled()
            && cir.getReturnValue()
            && ((AdvancementScreenAccess) this.getScreen()).getSelectedTab() != (Object) this) {
            CursorController.getInstance().setSingleCycleFallbackCursor(CursorType.POINTER);
        }
    }
}
