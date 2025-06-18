package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
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
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V"
    ))
    public void forceDefaultCursor(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (this.scrolling && CursorTypeUtil.isLeftClickHeld()) {
            CursorController.getInstance().setSingleCycleCursor(CursorType.DEFAULT_FORCE);
        }
    }
}
