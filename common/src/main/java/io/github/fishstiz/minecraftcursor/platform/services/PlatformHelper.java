package io.github.fishstiz.minecraftcursor.platform.services;

import io.github.fishstiz.minecraftcursor.api.MinecraftCursorInitializer;

import java.util.List;

public interface PlatformHelper {
    boolean isModLoaded(String modId);

    String getConfigDir();

    List<MinecraftCursorInitializer> getEntrypoints();

    default String mapClassName(String namespace, String className) {
        return className;
    }

    default String unmapClassName(String namespace, String className) {
        return className;
    }
}
