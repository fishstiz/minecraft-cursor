package io.github.fishstiz.minecraftcursor.mixin.client;

import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.screen.LoomScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LoomScreen.class)
public interface LoomScreenAccessor extends HandledScreenAccessor<LoomScreenHandler> {
    @Accessor("canApplyDyePattern")
    boolean getCanApplyDyePattern();

    @Accessor("visibleTopRow")
    int getVisibleTopRow();
}
