package io.github.fishstiz.minecraftcursor.mixin.compat.ftblibrary;

import dev.ftb.mods.ftblibrary.ui.CursorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(value = CursorType.class, remap = false)
public interface CursorTypeAccess {
    @Accessor("shape")
    int minecraft_cursor$getShape();

    @Accessor("cursor")
    long minecraft_cursor$getCursor();
}
