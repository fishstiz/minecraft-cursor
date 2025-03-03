package io.github.fishstiz.minecraftcursor;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

class CursorResourceReloadListener extends AbstractCursorResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    @Override
    public ResourceLocation getFabricId() {
        return getId();
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        reloadMinecraftCursor(manager);
    }
}
