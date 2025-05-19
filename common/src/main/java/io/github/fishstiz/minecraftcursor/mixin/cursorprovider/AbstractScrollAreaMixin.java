package io.github.fishstiz.minecraftcursor.mixin.cursorprovider;

import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollArea;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractScrollArea.class)
public abstract class AbstractScrollAreaMixin {
    @Shadow
    private boolean scrolling;

    @Inject(method = "renderScrollbar", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V"
    ))
    public void forceDefaultCursor(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (this.scrolling) {
            CursorController.getInstance().setSingleCycleCursor(CursorType.DEFAULT_FORCE);
        }
    }
}
