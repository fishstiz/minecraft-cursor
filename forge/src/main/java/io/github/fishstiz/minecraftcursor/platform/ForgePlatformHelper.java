package io.github.fishstiz.minecraftcursor.platform;

import cpw.mods.modlauncher.api.INameMappingService;
import io.github.fishstiz.minecraftcursor.api.MinecraftCursorInitializer;
import io.github.fishstiz.minecraftcursor.impl.MinecraftCursorInitializerImpl;
import io.github.fishstiz.minecraftcursor.platform.services.PlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.tools.obfuscation.ObfuscationData;

import java.nio.file.Path;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

public class ForgePlatformHelper implements PlatformHelper {
    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public @NotNull Platform getPlatform() {
        return Platform.FORGE;
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public List<MinecraftCursorInitializer> getEntrypoints() {
        ServiceLoader<MinecraftCursorInitializer> loader = ServiceLoader.load(MinecraftCursorInitializer.class);

        return StreamSupport.stream(loader.spliterator(), false)
                .filter(service -> !(service instanceof MinecraftCursorInitializerImpl))
                .toList();
    }

    @Override
    public String mapClassName(String namespace, String className) {
        return ObfuscationReflectionHelper.remapName(INameMappingService.Domain.CLASS, className);
    }
}
