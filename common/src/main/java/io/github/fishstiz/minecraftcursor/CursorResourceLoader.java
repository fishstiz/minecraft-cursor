package io.github.fishstiz.minecraftcursor;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.AnimationData;
import io.github.fishstiz.minecraftcursor.config.Config;
import io.github.fishstiz.minecraftcursor.config.ConfigLoader;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.util.SettingsUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;
import static io.github.fishstiz.minecraftcursor.MinecraftCursor.LOGGER;

public class CursorResourceLoader {
    private static final String ANIMATION_TYPE = ".mcmeta";
    private static final ResourceLocation SETTINGS_LOCATION = MinecraftCursor.loc("atlases/cursors.json");
    private static final ResourceLocation DIR = MinecraftCursor.loc("textures/cursors/");
    private static volatile Config.Resource resourceConfig;

    private CursorResourceLoader() {
    }

    public static ResourceLocation getDirectory() {
        return DIR;
    }

    static void reloadSettings(ResourceManager manager) {
        onReload();
        loadResourceSettings(manager);
        Minecraft.getInstance().execute(() -> {
            onReload();
            CursorManager.INSTANCE.reapplyCursor();
        });
    }

    public static void reload() {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();

        onReload();
        loadResourceSettings(manager);

        for (Cursor cursor : CursorManager.INSTANCE.getCursors()) {
            Config.Settings settings = CONFIG.getOrCreateSettings(cursor);
            loadCursorTexture(manager, cursor, settings);
        }

        onReload();
        CursorManager.INSTANCE.reapplyCursor();
    }

    static void onReload() {
        CursorManager.INSTANCE.setCurrentCursor(CursorType.DEFAULT);
    }

    private static void loadResourceSettings(ResourceManager manager) {
        getLayeredSettings(manager.getResourceStack(SETTINGS_LOCATION)).ifPresent(config -> {
            if (config.isDifferent(CONFIG)) {
                LOGGER.info("[minecraft-cursor] New resource pack settings detected, updating config...");
                CONFIG.setHash(config.getHash());
                CONFIG.merge(config);
                CONFIG.getGlobal().setActiveAll(false);
                CONFIG.save();
            }
            resourceConfig = config;
        });

        CursorManager.INSTANCE.getCursors().forEach(cursor -> cursor.setLazy(true));
    }

    private static Optional<Config.Resource> getLayeredSettings(List<Resource> configResources) {
        Config.Resource layeredResources = null;
        for (Resource configResource : configResources) {
            try (InputStream stream = configResource.open()) {
                Config.Resource resourceConfig = ConfigLoader.loadResource(stream);
                if (layeredResources == null) {
                    layeredResources = resourceConfig;
                } else {
                    layeredResources.layer(resourceConfig.getAllSettings());
                }
            } catch (IOException e) {
                LOGGER.error("[minecraft-cursor] Failed to load settings of resource pack '{}'", configResource.sourcePackId());
            }
        }
        return Optional.ofNullable(layeredResources);
    }

    public static boolean isResourceSetting(@NotNull Cursor cursor, @Nullable Config.Settings settings) {
        return SettingsUtil.equalSettings(resourceConfig.getOrCreateSettings(cursor), settings, true);
    }

    public static boolean retoreActiveResourceSettings(@NotNull Cursor cursor) {
        if (cursor.isLoaded() && resourceConfig != null) {
            CONFIG.replaceActiveSettings(resourceConfig, cursor);
            cursor.apply(CONFIG.getGlobal().apply(CONFIG.getOrCreateSettings(cursor)));
            return true;
        } else {
            LOGGER.error("Failed to apply resource settings for '{}'", cursor.getTypeKey());
        }
        return false;
    }

    public static void restoreResourceSettings() {
        if (resourceConfig != null) {
            CONFIG.setHash(resourceConfig.getHash());
            CONFIG.merge(resourceConfig);
            for (Cursor cursor : CursorManager.INSTANCE.getCursors()) {
                cursor.apply(CONFIG.getGlobal().apply(CONFIG.getOrCreateSettings(cursor)));
            }
        } else {
            LOGGER.error("Failed to apply resource config: Not Found.");
        }
    }

    public static boolean loadCursorTexture(ResourceManager manager, Cursor cursor) {
        return loadCursorTexture(manager, cursor, CONFIG.getOrCreateSettings(cursor));
    }

    public static boolean loadCursorTexture(Cursor cursor) {
        return loadCursorTexture(Minecraft.getInstance().getResourceManager(), cursor);
    }

    private static boolean loadCursorTexture(ResourceManager manager, Cursor cursor, Config.Settings settings) {
        LOGGER.info("[minecraft-cursor] Loading {} cursor...", cursor.getTypeKey());

        ResourceLocation location = cursor.getLocation();
        Optional<Resource> cursorResource = manager.getResource(location);

        try {
            if (cursorResource.isEmpty()) {
                LOGGER.error("[minecraft-cursor] Cursor Type: '{}' not found", cursor.getTypeKey());
                cursor.destroy();
                return false;
            }

            try (InputStream cursorStream = cursorResource.get().open(); NativeImage image = NativeImage.read(cursorStream)) {
                AnimationData animation = loadAnimation(manager, location, cursorResource.get());
                cursor = CursorManager.INSTANCE.loadCursor(cursor, image, CONFIG.getGlobal().apply(settings), animation);
                return true;
            } catch (IOException e) {
                LOGGER.error("[minecraft-cursor] Failed to load cursor at '{}': {}", location, e.getMessage());
                return false;
            }
        } finally {
            Minecraft.getInstance().execute(() -> Minecraft.getInstance().getTextureManager().release(location));
            cursor.setLazy(false);
        }
    }

    private static AnimationData loadAnimation(ResourceManager manager, ResourceLocation location, Resource cursorResource) {
        Optional<Resource> animationResource = manager.getResource(location.withSuffix(ANIMATION_TYPE));
        if (animationResource.isPresent() && animationResource.get().sourcePackId().equals(cursorResource.sourcePackId())) {
            try (InputStream stream = animationResource.get().open()) {
                return ConfigLoader.loadAnimationData(stream);
            } catch (IOException e) {
                LOGGER.error("[minecraft-cursor] Failed to load animation data for '{}'", location);
            }
        }
        return null;
    }
}
