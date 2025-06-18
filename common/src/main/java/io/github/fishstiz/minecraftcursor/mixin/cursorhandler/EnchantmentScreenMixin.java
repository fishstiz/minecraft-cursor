package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(EnchantmentScreen.class)
public abstract class EnchantmentScreenMixin extends AbstractContainerScreenMixin<EnchantmentMenu> {
    @Shadow @Final private static ResourceLocation ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE;

    protected EnchantmentScreenMixin(Component title) {
        super(title);
    }

    @WrapOperation(
            method = "renderBg",
            slice = @Slice(from = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawWordWrap(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/FormattedText;IIIIZ)V"
            )),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V"
            )
    )
    private void setPointerOnHighlight(GuiGraphics instance, RenderPipeline renderPipeline, ResourceLocation resourceLocation, int x, int y, int width, int height, Operation<Void> original) {
        original.call(instance, renderPipeline, resourceLocation, x, y, width, height);
        if (MinecraftCursor.CONFIG.isEnchantmentsEnabled() && resourceLocation == ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE) {
            CursorController.getInstance().setSingleCycleFallbackCursor(CursorType.POINTER);
        }
    }
}
