package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.gui.screen.ConfigurationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@Mod(value = MinecraftCursorNeoforge.MOD_ID, dist = Dist.CLIENT)
public class MinecraftCursorNeoforge {
    // Neoforge does not support dashes in mod id
    public static final String MOD_ID = "minecraft_cursor";

    public MinecraftCursorNeoforge(ModContainer container, IEventBus modEventBus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (c, screen) ->
                new ConfigurationScreen(screen)
        );
        modEventBus.addListener(AddPackFindersEvent.class, event -> event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "resourcepacks/cursors_extended"),
                PackType.CLIENT_RESOURCES,
                Component.literal("Cursors Extended"),
                PackSource.BUILT_IN,
                false,
                Pack.Position.TOP
        ));
        modEventBus.addListener(InitializeClientRegistriesEvent.class, event ->
                MinecraftCursor.init()
        );
        modEventBus.addListener(AddClientReloadListenersEvent.class, event -> {
            var listener = new CursorResourceReloadListener();
            event.addListener(listener.getId(), listener);
        });

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    private void afterClientTick(ClientTickEvent.Post event) {
        MinecraftCursor.afterClientTick(Minecraft.getInstance());
    }

    @SubscribeEvent
    private void afterScreenRender(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();
        MinecraftCursor.afterCurrentScreenRender(screen.getMinecraft(), screen, event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
    }
}
