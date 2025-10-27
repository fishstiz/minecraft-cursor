package io.github.fishstiz.minecraftcursor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

class CursorResourceReloadListener implements PreparableReloadListener {
    @Override
    public @NotNull CompletableFuture<Void> reload(
            PreparationBarrier barrier,
            @NotNull ResourceManager manager,
            @NotNull ProfilerFiller preparationProfiler,
            @NotNull ProfilerFiller reloadProfiler,
            @NotNull Executor backgroundExecutor,
            @NotNull Executor gameExecutor
    ) {
        return CompletableFuture.runAsync(() -> CursorResourceLoader.reloadSettings(manager), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenRunAsync(CursorResourceLoader::onReload, gameExecutor);
    }

    public ResourceLocation getId() {
        return CursorResourceLoader.getDirectory();
    }
}
