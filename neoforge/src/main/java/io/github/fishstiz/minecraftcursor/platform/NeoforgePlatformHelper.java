package io.github.fishstiz.minecraftcursor.platform;

import io.github.fishstiz.minecraftcursor.api.MinecraftCursorInitializer;
import io.github.fishstiz.minecraftcursor.impl.MinecraftCursorInitializerImpl;
import io.github.fishstiz.minecraftcursor.platform.services.PlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

public class NeoforgePlatformHelper implements PlatformHelper {
    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public String getConfigDir() {
        return FMLPaths.CONFIGDIR.get().toString();
    }

    @Override
    public List<MinecraftCursorInitializer> getEntrypoints() {
        ServiceLoader<MinecraftCursorInitializer> loader = ServiceLoader.load(MinecraftCursorInitializer.class);

        return StreamSupport.stream(loader.spliterator(), false)
                .filter(service -> !(service instanceof MinecraftCursorInitializerImpl))
                .toList();
    }
}
