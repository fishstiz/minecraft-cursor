package io.github.fishstiz.minecraftcursor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

class CursorResourceReloadListener implements PreparableReloadListener {
    @Override
    public @NotNull CompletableFuture<Void> reload(
            PreparationBarrier barrier,
            @NotNull ResourceManager manager,
            @NotNull Executor backgroundExecutor,
            @NotNull Executor gameExecutor
    ) {
        return CompletableFuture.runAsync(() -> CursorLoader.reload(manager))
                .thenCompose(barrier::wait)
                .thenRunAsync(CursorLoader::applyDefaultCursor, gameExecutor);
    }

    public ResourceLocation getId() {
        return CursorLoader.getLocation();
    }
}
