package io.github.fishstiz.minecraftcursor.mixin.client;

import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BookEditScreen.class)
public interface BookEditScreenAccessor {
    @Accessor("finalizeButton")
    ButtonWidget getFinalizeButton();

    @Invoker("screenPositionToAbsolutePosition")
    BookEditScreen.Position invokeScreenPosToAbsPos(BookEditScreen.Position position);
}
