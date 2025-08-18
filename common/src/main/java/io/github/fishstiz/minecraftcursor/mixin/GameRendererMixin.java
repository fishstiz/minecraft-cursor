package io.github.fishstiz.minecraftcursor.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/toasts/ToastManager;render(Lnet/minecraft/client/gui/GuiGraphics;)V",
            shift = At.Shift.AFTER
    ))
    private void renderCursor(
            DeltaTracker deltaTracker,
            boolean renderLevel,
            CallbackInfo ci,
            @Local(ordinal = 0) GuiGraphics guiGraphics,
            @Local(ordinal = 0) int mouseX,
            @Local(ordinal = 1) int mouseY
    ) {
        CursorManager.INSTANCE.renderCursor(this.minecraft, guiGraphics, mouseX, mouseY);
    }
}
