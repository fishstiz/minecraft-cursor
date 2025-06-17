package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.cursor.resolver.ElementInspector;
import io.github.fishstiz.minecraftcursor.gui.screen.ConfigurationScreen;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = MinecraftCursorNeoforge.MOD_ID, dist = Dist.CLIENT)
public class MinecraftCursorNeoforge {
    // Neoforge does not support dashes in mod id
    public static final String MOD_ID = "minecraft_cursor";

    public MinecraftCursorNeoforge(ModContainer container, IEventBus modEventBus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (c, screen) ->
                new ConfigurationScreen(screen)
        );

        modEventBus.addListener(InitializeClientRegistriesEvent.class, event ->
                MinecraftCursor.init()
        );
        modEventBus.addListener(AddClientReloadListenersEvent.class, event -> {
            var listener = new CursorResourceReloadListener();
            event.addListener(listener.getId(), listener);
        });
        modEventBus.addListener(RegisterGuiLayersEvent.class, event ->
                event.registerAboveAll(ElementInspector.getId(), this::onGuiRender)
        );

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onScreenInit(ScreenEvent.Init.Pre event) {
        Screen screen = event.getScreen();
        MinecraftCursor.beforeScreenInit(screen.getMinecraft(), screen);
    }

    @SubscribeEvent
    public void onScreenRender(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();
        MinecraftCursor.afterScreenRender(screen.getMinecraft(), screen, event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        MinecraftCursor.afterClientTick(Minecraft.getInstance());
    }

    private void onGuiRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        MinecraftCursor.renderInspector(Minecraft.getInstance(), guiGraphics);
    }
}
