package io.github.fishstiz.minecraftcursor;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

class CursorResourceReloadListener extends CursorResourceListener implements SimpleSynchronousResourceReloadListener {
    @Override
    public ResourceLocation getFabricId() {
        return getId();
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        System.out.println("RELOADING!");
        reload(manager);

        Resource resource = manager.getResource(ResourceLocation.fromNamespaceAndPath("minecraft-cursor", "textures/default.png"))
                .orElse(null);

        if (resource == null) {
            MinecraftCursor.LOGGER.info("RESOURCE NULL!!!!!!!");
        } else {
            MinecraftCursor.LOGGER.info("RESOURCE ACTUALLLY NOT NULL!!!!!!!");
        }
    }
}
