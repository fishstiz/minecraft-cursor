package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoomScreen.class)
public abstract class LoomScreenMixin extends AbstractContainerScreenMixin<LoomMenu> {
    @Unique
    private static final int minecraft_cursor$HIGHLIGHTED_V_OFFSET = 28;

    @Shadow
    private boolean scrolling;

    @Shadow
    @Final
    private static ResourceLocation BG_LOCATION;

    protected LoomScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "renderBg", at = @At("RETURN"))
    private void forceDefaultOnScroll(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.scrolling && CursorTypeUtil.isLeftClickHeld()) {
            CursorController.getInstance().setSingleCycleCursor(CursorType.DEFAULT_FORCE);
        }
    }

    @ModifyArg(
            method = "renderBg",
            slice = @Slice(from = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/LoomMenu;getSelectablePatterns()Ljava/util/List;"
            )),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"
            )
    )
    private ResourceLocation setPointerOnHover(ResourceLocation resourceLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        if (MinecraftCursor.CONFIG.isLoomPatternsEnabled()
            && resourceLocation == BG_LOCATION
            && vOffset == this.imageHeight + minecraft_cursor$HIGHLIGHTED_V_OFFSET) {
            CursorController.getInstance().setSingleCycleFallbackCursor(CursorType.POINTER);
        }
        return resourceLocation;
    }
}
