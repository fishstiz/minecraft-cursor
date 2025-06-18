package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

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

    @ModifyArg(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V",
            ordinal = 0
    ))
    private ResourceLocation setTextCursorOnHover(ResourceLocation resourceLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        if (MinecraftCursor.CONFIG.isBookEditEnabled() && !this.isSigning && resourceLocation == BookViewScreen.BOOK_LOCATION) {
            var minecraft = Minecraft.getInstance();
            var window = minecraft.getWindow();
            double mouseX = minecraft.mouseHandler.xpos() * window.getGuiScaledWidth() / window.getScreenWidth();
            double mouseY = minecraft.mouseHandler.ypos() * window.getGuiScaledHeight() / window.getScreenHeight();

            int left = x + minecraft_cursor$TEXT_OFFSET_X;
            int top = y + minecraft_cursor$TEXT_OFFSET_Y;
            int right = left + TEXT_WIDTH;
            int bottom = top + TEXT_HEIGHT;

            if (mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom) {
                CursorController.getInstance().setSingleCycleFallbackCursor(CursorType.TEXT);
            }
        }
        return resourceLocation;
    }
}
