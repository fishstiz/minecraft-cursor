package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(EnchantmentScreen.class)
public abstract class EnchantmentScreenMixin extends AbstractContainerScreenMixin<EnchantmentMenu> {
    @Shadow
    @Final
    private static ResourceLocation ENCHANTING_TABLE_LOCATION;

    @Unique
    private static final int minecraft_cursor$HIGHLIGHTED_V_OFFSET = 204;

    protected EnchantmentScreenMixin(Component title) {
        super(title);
    }

    @ModifyArg(
            method = "renderBg",
            slice = @Slice(from = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawWordWrap(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/FormattedText;IIII)V"
            )),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"
            )
    )
    private ResourceLocation setPointerOnHighlight(ResourceLocation resourceLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        if (MinecraftCursor.CONFIG.isEnchantmentsEnabled()
            && resourceLocation == ENCHANTING_TABLE_LOCATION
            && vOffset == minecraft_cursor$HIGHLIGHTED_V_OFFSET) {
            CursorController.getInstance().setSingleCycleFallbackCursor(CursorType.POINTER);
        }
        return resourceLocation;
    }
}
