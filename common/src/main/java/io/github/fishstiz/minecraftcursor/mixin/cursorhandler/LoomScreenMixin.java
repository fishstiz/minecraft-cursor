package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.LoomMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoomScreen.class)
public abstract class LoomScreenMixin extends AbstractContainerScreenMixin<LoomMenu> {
    @Shadow
    private boolean scrolling;

    @Shadow
    @Final
    private static ResourceLocation PATTERN_HIGHLIGHTED_SPRITE;

    protected LoomScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "renderBg", at = @At("RETURN"))
    private void forceDefaultOnScroll(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.scrolling && CursorTypeUtil.isLeftClickHeld()) {
            CursorController.getInstance().setSingleCycleCursor(CursorType.DEFAULT_FORCE);
        }
    }

    @WrapOperation(
            method = "renderBg",
            slice = @Slice(from = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/LoomMenu;getSelectablePatterns()Ljava/util/List;"
            )),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V"
            )
    )
    private void setPointerOnHighlight(GuiGraphics instance, RenderPipeline renderPipeline, ResourceLocation resourceLocation, int x, int y, int width, int height, Operation<Void> original) {
        original.call(instance, renderPipeline, resourceLocation, x, y, width, height);
        if (MinecraftCursor.CONFIG.isLoomPatternsEnabled() && resourceLocation == PATTERN_HIGHLIGHTED_SPRITE) {
            CursorController.getInstance().setSingleCycleFallbackCursor(CursorType.POINTER);
        }
    }
}
