package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.StonecutterMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(StonecutterScreen.class)
public abstract class StonecutterScreenMixin extends AbstractContainerScreenMixin<StonecutterMenu> {
    @Unique
    private static final int minecraft_cursor$HIGHLIGHTED_V_OFFSET = 36;

    @Shadow
    @Final
    private static ResourceLocation BG_LOCATION;

    protected StonecutterScreenMixin(Component title) {
        super(title);
    }

    @ModifyArg(method = "renderButtons", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"
    ))
    private ResourceLocation setPointerOnHover(ResourceLocation resourceLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        if (MinecraftCursor.CONFIG.isStonecutterRecipesEnabled()
            && resourceLocation == BG_LOCATION
            && vOffset == this.imageHeight + minecraft_cursor$HIGHLIGHTED_V_OFFSET) {
            CursorController.getInstance().setSingleCycleFallbackCursor(CursorType.POINTER);
        }
        return resourceLocation;
    }
}
