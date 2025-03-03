package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.gui.screen.CursorOptionsScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = MinecraftCursorNeoforge.MOD_ID, dist = Dist.CLIENT)
public class MinecraftCursorNeoforge {
    // Neoforge does not support dashes in mod id
    public static final String MOD_ID = "minecraft_cursor";

    public MinecraftCursorNeoforge(ModContainer container, IEventBus modEventBus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (c, screen) ->
                new CursorOptionsScreen(screen, CursorManager.INSTANCE));

        MinecraftCursor.init();

        modEventBus.addListener(AddClientReloadListenersEvent.class, event -> {
            var listener = new CursorResourceReloadListener();
            event.addListener(listener.getId(), listener);
        });
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onScreenInit(ScreenEvent.Init.Pre event) {
            MinecraftCursor.getInstance().beforeScreenInit(event.getScreen());
        }

        @SubscribeEvent
        public static void onScreenRender(ScreenEvent.Render.Post event) {
            MinecraftCursor.getInstance().afterRenderScreen(event.getMouseX(), event.getMouseY());
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            MinecraftCursor.getInstance().tick();
        }
    }
}
