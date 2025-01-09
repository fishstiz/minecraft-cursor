package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.config.MinecraftCursorConfigManager;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;

public class MinecraftCursorClient implements ClientModInitializer {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final MinecraftCursorHandler cursorHandler = new MinecraftCursorHandler(client);
    private static final MinecraftCursorConfigManager configManager = new MinecraftCursorConfigManager(MinecraftCursor.MOD_ID);

    public static MinecraftCursorHandler getCursorHandler() {
        return cursorHandler;
    }

    public static MinecraftCursorConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public void onInitializeClient() {
        MinecraftCursorResourceReloadListener resourceReloadListener = new MinecraftCursorResourceReloadListener(cursorHandler, configManager);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(resourceReloadListener);
    }
}