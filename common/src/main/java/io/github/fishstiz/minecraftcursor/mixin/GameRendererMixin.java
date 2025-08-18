package io.github.fishstiz.minecraftcursor.mixin;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    Minecraft minecraft;

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;flush()V"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void renderCursor(
            float partialTicks,
            long nanoTime,
            boolean renderLevel,
            CallbackInfo ci,
            int i,
            int j,
            Window window,
            Matrix4f matrix4f,
            PoseStack posestack,
            GuiGraphics guigraphics
    ) {
        CursorManager.INSTANCE.renderCursor(this.minecraft, guigraphics, i, j);
    }
}
