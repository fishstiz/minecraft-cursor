package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.cursor.resolver.ElementInspector;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class MinecraftCursorFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MinecraftCursor.init();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new CursorResourceReloadListener());
        ScreenEvents.BEFORE_INIT.register(this::onScreenInit);
        ClientTickEvents.START_CLIENT_TICK.register(MinecraftCursor::afterClientTick);
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.addLayer(new InspectorLayer()));
    }

    private void onScreenInit(Minecraft minecraft, Screen screen, int width, int height) {
        MinecraftCursor.beforeScreenInit(minecraft, screen);
        ScreenEvents.afterRender(screen).register(this::onScreenRender);
    }

    private void onScreenRender(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        MinecraftCursor.afterScreenRender(Minecraft.getInstance(), screen, guiGraphics, mouseX, mouseY);
    }

    private static class InspectorLayer implements IdentifiedLayer {
        @Override
        public ResourceLocation id() {
            return ElementInspector.getId();
        }

        @Override
        public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
            MinecraftCursor.renderInspector(Minecraft.getInstance(), guiGraphics);
        }
    }
}
