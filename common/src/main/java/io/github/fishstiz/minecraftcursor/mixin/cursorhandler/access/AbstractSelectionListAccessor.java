package io.github.fishstiz.minecraftcursor.mixin.cursorhandler.access;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractSelectionList.class)
public interface AbstractSelectionListAccessor {
    @Invoker("getRowTop")
    int invokeGetRowTop(int index);
}
