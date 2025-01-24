package io.github.fishstiz.minecraftcursor.mixin.client.access;

import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.screen.StonecutterScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StonecutterScreen.class)
public interface StonecutterScreenAccessor extends HandledScreenAccessor<StonecutterScreenHandler> {
    @Accessor("scrollOffset")
    int getScrollOffset();
}
