package io.github.fishstiz.minecraftcursor.platform;

import io.github.fishstiz.minecraftcursor.platform.services.PlatformHelper;

import java.util.ServiceLoader;

public class Services {
    private Services() {
    }

    public static final PlatformHelper PLATFORM = load(PlatformHelper.class);

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}