package io.github.fishstiz.minecraftcursor;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.AnimatedCursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfigLoader;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;
import static io.github.fishstiz.minecraftcursor.MinecraftCursor.LOGGER;
import static io.github.fishstiz.minecraftcursor.MinecraftCursor.MOD_ID;

public class CursorLoader {
    private static final String ANIMATION_TYPE = ".mcmeta";
    private static final String CONFIG_PATH = "atlases/cursors.json";
    private static final ResourceLocation DIR = new ResourceLocation(MOD_ID, "textures/cursors/");
    private static CursorConfig resourceConfig;

    private CursorLoader() {
    }

    public static ResourceLocation getDirectory() {
        return DIR;
    }

    public static void reload(ResourceManager manager) {
        onReload();
        loadResourceSettings(manager);
        loadCursorTextures(manager);
        Minecraft.getInstance().execute(CursorLoader::onReload);
    }

    static void onReload() {
        CursorManager.INSTANCE.setCurrentCursor(CursorType.DEFAULT);
    }

    private static void loadResourceSettings(ResourceManager manager) {
        List<Resource> configResources = manager.getResourceStack(new ResourceLocation(MOD_ID, CONFIG_PATH));

        if (configResources.isEmpty()) return;

        getLayeredSettings(configResources).ifPresent(config -> {
            if (!config.get_hash().equals(CONFIG.get_hash())) {
                LOGGER.info("[minecraft-cursor] New resource pack settings detected, updating config...");
                CONFIG.set_hash(config.get_hash());
                CONFIG.mergeSettings(config.getSettings());
                CONFIG.getGlobal().setActiveAll(false);
                CONFIG.save();
            }
            resourceConfig = config;
        });
    }

    private static Optional<CursorConfig> getLayeredSettings(List<Resource> configResources) {
        CursorConfig layeredConfig = null;

        for (Resource configResource : configResources) {
            try (InputStream stream = configResource.open()) {
                CursorConfig loadedConfig = CursorConfigLoader.fromStream(stream);

                if (layeredConfig == null) {
                    layeredConfig = loadedConfig;
                } else {
                    layeredConfig.layerSettings(loadedConfig.getSettings());
                }
            } catch (IOException e) {
                LOGGER.error("[minecraft-cursor] Failed to load settings of resource pack '{}'", configResource.sourcePackId());
            }
        }

        return Optional.ofNullable(layeredConfig);
    }

    private static void loadCursorTextures(ResourceManager manager) {
        for (Cursor cursor : CursorManager.INSTANCE.getCursors()) {
            CursorConfig.Settings settings = CONFIG.getOrCreateCursorSettings(cursor.getType());
            if (settings.isEnabled()) {
                loadCursorTexture(manager, cursor, settings);
            } else {
                LOGGER.info("[minecraft-cursor] Skipped disabled cursor '{}'", cursor.getTypeKey());
            }
        }
    }

    public static boolean loadCursorTexture(Cursor cursor) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!loadCursorTexture(minecraft.getResourceManager(), cursor, CONFIG.getOrCreateCursorSettings(cursor.getType()))) {
            minecraft.getToasts().addToast(SystemToast.multiline(
                    minecraft,
                    SystemToast.SystemToastIds.PACK_LOAD_FAILURE,
                    Component.translatable("resourcePack.load_fail"),
                    cursor.getText()
            ));
            return false;
        }
        return true;
    }

    private static boolean loadCursorTexture(ResourceManager manager, Cursor cursor, CursorConfig.Settings settings) {
        ResourceLocation location = cursor.getLocation();
        Optional<Resource> cursorResource = manager.getResource(location);

        if (cursorResource.isEmpty()) {
            LOGGER.error("[minecraft-cursor] Cursor Type: '{}' not found", cursor.getTypeKey());
            return false;
        }

        try (InputStream cursorStream = cursorResource.get().open(); NativeImage image = NativeImage.read(cursorStream)) {
            LOGGER.info("[minecraft-cursor] Loading cursor '{}'...", cursor.getTypeKey());
            AnimatedCursorConfig animation = loadAnimation(manager, location, cursorResource.get());
            CursorManager.INSTANCE.loadCursor(cursor, image, getSettingsWithGlobal(settings), animation);
            return true;
        } catch (IOException e) {
            LOGGER.error("[minecraft-cursor] Failed to load cursor at '{}'", location);
            return false;
        }
    }

    private static AnimatedCursorConfig loadAnimation(ResourceManager manager, ResourceLocation location, Resource cursorResource) {
        Optional<Resource> animationResource = manager.getResource(location.withSuffix(ANIMATION_TYPE));
        if (animationResource.isPresent() && animationResource.get().sourcePackId().equals(cursorResource.sourcePackId())) {
            try (InputStream stream = animationResource.get().open()) {
                return CursorConfigLoader.getAnimationConfig(stream);
            } catch (IOException e) {
                LOGGER.error("[minecraft-cursor] Failed to load animation config for '{}'", location);
            }
        }
        return null;
    }

    private static CursorConfig.Settings getSettingsWithGlobal(CursorConfig.Settings base) {
        CursorConfig.Settings settings = new CursorConfig.Settings();
        CursorConfig.GlobalSettings global = CONFIG.getGlobal();

        settings.update(
                global.isScaleActive() ? global.getScale() : base.getScale(),
                global.isXHotActive() ? global.getXHot() : base.getXHot(),
                global.isYHotActive() ? global.getYHot() : base.getYHot(),
                base.isEnabled()
        );

        if (base.isAnimated() != null) {
            settings.setAnimated(base.isAnimated());
        }

        return settings;
    }

    public static void resetSettings() {
        if (resourceConfig != null) {
            CONFIG.set_hash(resourceConfig.get_hash());
            CONFIG.layerSettings(resourceConfig.getSettings());

            for (Cursor cursor : CursorManager.INSTANCE.getCursors()) {
                cursor.applySettings(CONFIG.getOrCreateCursorSettings(cursor.getType()).copy());
            }
        } else {
            LOGGER.error("Failed to apply resource config: Not Found.");
        }
    }
}
