package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.gui.screen.CursorOptionsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = MinecraftCursorNeoforge.MOD_ID, dist = Dist.CLIENT)
public class MinecraftCursorNeoforge {
    // Neoforge does not support dashes in mod id
    public static final String MOD_ID = "minecraft_cursor";

    public MinecraftCursorNeoforge(ModContainer container, IEventBus modEventBus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (c, screen) ->
                new CursorOptionsScreen(screen)
        );

        modEventBus.addListener(RegisterClientReloadListenersEvent.class, event ->
                event.registerReloadListener(new CursorResourceReloadListener()));

        MinecraftCursor.init();
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onScreenInit(ScreenEvent.Init.Pre event) {
        Screen screen = event.getScreen();
        MinecraftCursor.onScreenInit(screen.getMinecraft(), screen);
    }

    @SubscribeEvent
    public void onScreenRender(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();
        MinecraftCursor.onScreenRender(screen.getMinecraft(), screen, event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        MinecraftCursor.onClientTick(MinecraftHolder.INSTANCE);
    }

    private static class MinecraftHolder {
        private static final Minecraft INSTANCE = Minecraft.getInstance();
    }
}
