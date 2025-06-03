package io.github.fishstiz.minecraftcursor.platform;

import io.github.fishstiz.minecraftcursor.api.MinecraftCursorInitializer;
import io.github.fishstiz.minecraftcursor.platform.services.PlatformHelper;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

public class NeoforgePlatformHelper implements PlatformHelper {
    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public List<MinecraftCursorInitializer> getEntrypoints() {
        return StreamSupport.stream(ServiceLoader.load(MinecraftCursorInitializer.class).spliterator(), false).toList();
    }
}
