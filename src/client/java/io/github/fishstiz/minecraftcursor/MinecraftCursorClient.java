package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.config.CursorConfigLoader;
import io.github.fishstiz.minecraftcursor.config.CursorConfigService;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;

public class MinecraftCursorClient implements ClientModInitializer {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final CursorConfigService CONFIG = new CursorConfigService(String.format("config/%s%s", MinecraftCursor.MOD_ID, CursorConfigLoader.FILE_EXTENSION));
    private static final CursorManager CURSOR_MANAGER = new CursorManager(CONFIG, CLIENT);
    private static final CursorResourceReloadListener RESOURCE_RELOAD_LISTENER = new CursorResourceReloadListener(CURSOR_MANAGER, MinecraftCursor.MOD_ID);

    public static CursorManager getCursorManager() {
        return CURSOR_MANAGER;
    }

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(RESOURCE_RELOAD_LISTENER);
        CursorTypeRegistry.init();
    }
}