package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(BookEditScreen.class)
public abstract class BookEditScreenMixin {
    @Shadow
    @Final
    private static int TEXT_WIDTH;

    @Shadow
    @Final
    private static int TEXT_HEIGHT;

    @Shadow
    private boolean isSigning;

    @Unique
    private static final int minecraft_cursor$TEXT_OFFSET_X = 36;

    @Unique
    private static final int minecraft_cursor$TEXT_OFFSET_Y = 28;

    @WrapOperation(method = "renderBackground", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"
    ))
    private void setTextCursorOnHover(
            GuiGraphics instance,
            Function<ResourceLocation, RenderType> renderTypeGetter,
            ResourceLocation atlasLocation,
            int x, int y,
            float uOffset, float vOffset,
            int uWidth, int vHeight,
            int textureWidth, int textureHeight,
            Operation<Void> original,
            @Local(argsOnly = true, ordinal = 0) int mouseX,
            @Local(argsOnly = true, ordinal = 1) int mouseY
    ) {
        original.call(instance, renderTypeGetter, atlasLocation, x, y, uOffset, vOffset, uWidth, vHeight, textureWidth, textureWidth);

        if (MinecraftCursor.CONFIG.isBookEditEnabled() && !this.isSigning && atlasLocation == BookViewScreen.BOOK_LOCATION) {
            int left = x + minecraft_cursor$TEXT_OFFSET_X;
            int top = y + minecraft_cursor$TEXT_OFFSET_Y;
            int right = left + TEXT_WIDTH;
            int bottom = top + TEXT_HEIGHT;

            if (mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom) {
                CursorController.getInstance().setSingleCycleFallbackCursor(CursorType.TEXT);
            }
        }
    }
}
