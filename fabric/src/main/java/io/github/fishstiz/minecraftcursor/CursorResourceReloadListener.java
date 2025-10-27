package io.github.fishstiz.minecraftcursor;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

class CursorResourceReloadListener implements IdentifiableResourceReloadListener {
    @Override
    public ResourceLocation getFabricId() {
        return CursorResourceLoader.getDirectory();
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(
            PreparationBarrier barrier,
            ResourceManager manager,
            Executor backgroundExecutor,
            Executor gameExecutor
    ) {
        return CompletableFuture.runAsync(() -> CursorResourceLoader.reload(manager), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenRunAsync(CursorResourceLoader::onReload, gameExecutor);
    }
}
