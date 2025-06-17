package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.cursor.resolver.ElementInspector;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.packs.PackType;

public class MinecraftCursorFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MinecraftCursor.init();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new CursorResourceReloadListener());
        ScreenEvents.BEFORE_INIT.register(this::onScreenInit);
        ClientTickEvents.START_CLIENT_TICK.register(MinecraftCursor::afterClientTick);
        HudElementRegistry.addFirst(ElementInspector.getId(), this::onHudRender);
    }

    private void onScreenInit(Minecraft minecraft, Screen screen, int width, int height) {
        MinecraftCursor.beforeScreenInit(minecraft, screen);
        ScreenEvents.afterRender(screen).register(this::onScreenRender);
    }

    private void onScreenRender(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        MinecraftCursor.afterScreenRender(Minecraft.getInstance(), screen, guiGraphics, mouseX, mouseY);
    }

    private void onHudRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        MinecraftCursor.renderInspector(Minecraft.getInstance(), guiGraphics);
    }
}
