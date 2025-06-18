package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AdvancementsScreen.class)
public abstract class AdvancementScreenMixin {
    @Shadow
    @Nullable
    private AdvancementTab selectedTab;

    @WrapOperation(method = "renderTooltips", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;isMouseOver(IIDD)Z"
    ))
    private boolean setPointerOnHover(AdvancementTab tab, int offsetX, int offsetY, double mouseX, double mouseY, Operation<Boolean> original) {
        if (original.call(tab, offsetX, offsetY, mouseX, mouseY)) {
            if (MinecraftCursor.CONFIG.isAdvancementTabsEnabled() && tab != this.selectedTab) {
                CursorController.getInstance().setSingleCycleFallbackCursor(CursorType.POINTER);
            }
            return true;
        }
        return false;
    }
}
