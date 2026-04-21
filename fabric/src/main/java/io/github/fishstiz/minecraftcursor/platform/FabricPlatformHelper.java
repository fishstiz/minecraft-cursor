package io.github.fishstiz.minecraftcursor.platform;

import io.github.fishstiz.minecraftcursor.api.MinecraftCursorInitializer;
import io.github.fishstiz.minecraftcursor.platform.services.PlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.List;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.MOD_ID;

public class FabricPlatformHelper implements PlatformHelper {
    @Override
    public String getPlatform() {
        return "fabric";
    }

    @Override
    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public List<MinecraftCursorInitializer> getEntrypoints() {
        return FabricLoader.getInstance().getEntrypoints(MOD_ID, MinecraftCursorInitializer.class);
    }

    @Override
    public String mapClassName(String namespace, String className) {
        return FabricLoader.getInstance().getMappingResolver().mapClassName(namespace, className);
    }

    @Override
    public String unmapClassName(String namespace, String className) {
        return FabricLoader.getInstance().getMappingResolver().unmapClassName(namespace, className);
    }
}
