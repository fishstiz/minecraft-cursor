package io.github.fishstiz.minecraftcursor.mixin.access;

import io.github.fishstiz.minecraftcursor.mixin.access.HandledScreenAccessor;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.world.inventory.LoomMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LoomScreen.class)
public interface LoomScreenAccessor extends HandledScreenAccessor<LoomMenu> {
    @Accessor("displayPatterns")
    boolean getCanApplyDyePattern();

    @Accessor("startRow")
    int getVisibleTopRow();
}
