package io.github.fishstiz.minecraftcursor.mixin;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Shadow
    @Nullable
    protected Minecraft minecraft;

    @Inject(method = "render", at = @At("RETURN"))
    public void afterRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.minecraft != null) {
            MinecraftCursor.afterScreenRender(this.minecraft, (Screen) (Object) this, guiGraphics, mouseX, mouseY);
        }
    }
}
