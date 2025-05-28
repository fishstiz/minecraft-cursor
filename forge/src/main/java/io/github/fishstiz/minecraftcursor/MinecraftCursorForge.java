package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.gui.screen.ConfigurationScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(MinecraftCursorForge.MOD_ID)
public class MinecraftCursorForge {
    // Forge does not support dashes in mod id
    public static final String MOD_ID = "minecraft_cursor";

    @SuppressWarnings("removal")
    public MinecraftCursorForge() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ConfigurationScreen(screen))
        );

        MinecraftCursor.init();
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(new CursorResourceReloadListener());
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onScreenInit(ScreenEvent.Init.Pre event) {
            MinecraftCursor.onScreenInit(MinecraftHolder.INSTANCE, event.getScreen());
        }

        @SubscribeEvent
        public static void onScreenRender(ScreenEvent.Render.Post event) {
            MinecraftCursor.onScreenRender(MinecraftHolder.INSTANCE, event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                MinecraftCursor.onClientTick(MinecraftHolder.INSTANCE);
            }
        }
    }

    private static class MinecraftHolder {
        private static final Minecraft INSTANCE = Minecraft.getInstance();
    }
}
