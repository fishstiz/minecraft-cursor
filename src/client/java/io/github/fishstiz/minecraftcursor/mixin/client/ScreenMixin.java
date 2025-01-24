package io.github.fishstiz.minecraftcursor.mixin.client;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow @Final private List<Element> children;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(Text title, CallbackInfo ci) {
        MinecraftCursorClient.SCREEN_ELEMENTS.addAll(children);
    }
}
