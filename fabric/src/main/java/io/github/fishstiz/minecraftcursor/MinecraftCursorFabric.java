package io.github.fishstiz.minecraftcursor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.packs.PackType;

public class MinecraftCursorFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MinecraftCursor.init();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new CursorResourceReloadListener());
        ClientTickEvents.END_CLIENT_TICK.register(MinecraftCursor::afterClientTick);
        ScreenEvents.BEFORE_INIT.register(this::onScreenInit);
    }

    private void onScreenInit(Minecraft minecraft, Screen screen, int width, int height) {
        ScreenEvents.afterRender(screen).register(this::onScreenRender);
    }

    private void onScreenRender(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        MinecraftCursor.afterCurrentScreenRender(Minecraft.getInstance(), screen, guiGraphics, mouseX, mouseY);
    }
}
