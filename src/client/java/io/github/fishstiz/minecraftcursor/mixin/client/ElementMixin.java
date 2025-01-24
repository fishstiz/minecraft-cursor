package io.github.fishstiz.minecraftcursor.mixin.client;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClickableWidget.class)
public class ElementMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(int i, int j, int k, int l, Text text, CallbackInfo ci) {
        MinecraftCursorClient.SCREEN_ELEMENTS.add((Element) this);
    }
}
