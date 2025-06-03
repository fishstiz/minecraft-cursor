package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractScrollWidget.class)
public abstract class AbstractScrollWidgetMixin {
    @Shadow
    private boolean scrolling;

    @Shadow protected abstract boolean scrollbarVisible();

    @Inject(method = "renderScrollBar", at = @At(value = "TAIL"))
    public void forceDefaultCursor(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (this.scrolling && this.scrollbarVisible() && CursorTypeUtil.isLeftClickHeld()) {
            CursorController.getInstance().setSingleCycleCursor(CursorType.DEFAULT_FORCE);
        }
    }
}
