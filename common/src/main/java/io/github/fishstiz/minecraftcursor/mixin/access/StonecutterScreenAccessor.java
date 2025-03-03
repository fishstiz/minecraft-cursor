package io.github.fishstiz.minecraftcursor.mixin.access;

import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.world.inventory.StonecutterMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StonecutterScreen.class)
public interface StonecutterScreenAccessor extends HandledScreenAccessor<StonecutterMenu> {
    @Accessor("startIndex")
    int getScrollOffset();
}