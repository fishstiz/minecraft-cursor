package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.StonecutterMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(StonecutterScreen.class)
public abstract class StonecutterScreenMixin extends AbstractContainerScreenMixin<StonecutterMenu> {
    @Shadow
    @Final
    private static ResourceLocation RECIPE_HIGHLIGHTED_SPRITE;

    protected StonecutterScreenMixin(Component title) {
        super(title);
    }

    @WrapOperation(method = "renderButtons", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V"
    ))
    private void setPointerOnHover(GuiGraphics instance, Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation sprite, int x, int y, int width, int height, Operation<Void> original) {
        original.call(instance, renderTypeGetter, sprite, x, y, width, height);
        if (MinecraftCursor.CONFIG.isStonecutterRecipesEnabled() && sprite == RECIPE_HIGHLIGHTED_SPRITE) {
            CursorController.getInstance().setSingleCycleFallbackCursor(CursorType.POINTER);
        }
    }
}
