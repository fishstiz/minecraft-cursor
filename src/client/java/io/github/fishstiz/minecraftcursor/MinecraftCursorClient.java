package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;

public class MinecraftCursorClient implements ClientModInitializer {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final CursorConfig CONFIG = new CursorConfig();
    private static final CursorManager MANAGER = new CursorManager(CONFIG, CLIENT);
    private static final CursorResourceReloadListener RESOURCE_RELOAD_LISTENER = new CursorResourceReloadListener(MANAGER, MinecraftCursor.MOD_ID);

    public static CursorManager getManager() {
        return MANAGER;
    }

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(RESOURCE_RELOAD_LISTENER);
    }
}