package io.github.fishstiz.minecraftcursor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class MinecraftCursorClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
        MinecraftCursorResourceReloadListener resourceReloadListener = new MinecraftCursorResourceReloadListener();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(resourceReloadListener);
    }
}