package io.github.fishstiz.minecraftcursor.platform;

import cpw.mods.modlauncher.api.INameMappingService;
import io.github.fishstiz.minecraftcursor.api.MinecraftCursorInitializer;
import io.github.fishstiz.minecraftcursor.platform.services.PlatformHelper;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.nio.file.Path;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

public class ForgePlatformHelper implements PlatformHelper {
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

    @Override
    public String mapClassName(String namespace, String className) {
        return ObfuscationReflectionHelper.remapName(INameMappingService.Domain.CLASS, className);
    }
}
