package io.github.fishstiz.minecraftcursor.mixin.cursorhandler.access;

import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public interface OnlineServerEntryAccessor {
    @Accessor("screen")
    JoinMultiplayerScreen getScreen();

    @Invoker("canJoin")
    boolean invokeCanJoin();
}
