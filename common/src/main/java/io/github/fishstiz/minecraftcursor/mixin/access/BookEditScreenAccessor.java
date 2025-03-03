package io.github.fishstiz.minecraftcursor.mixin.access;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BookEditScreen.class)
public interface BookEditScreenAccessor {
    @Accessor("finalizeButton")
    Button getFinalizeButton();

    @Invoker("convertScreenToLocal")
    BookEditScreen.Pos2i invokeScreenPosToAbsPos(BookEditScreen.Pos2i position);
}
