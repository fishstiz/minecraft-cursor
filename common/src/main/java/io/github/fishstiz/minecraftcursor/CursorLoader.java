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
    private static final ResourceLocation SETTINGS_LOCATION = new ResourceLocation(MOD_ID, "atlases/cursors.json");
    private static final ResourceLocation DIR = new ResourceLocation(MOD_ID, "textures/cursors/");
    private static CursorConfig.Resource resourceConfig;

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
        getLayeredSettings(manager.getResourceStack(SETTINGS_LOCATION)).ifPresent(config -> {
            if (config.isDifferent(CONFIG)) {
                LOGGER.info("[minecraft-cursor] New resource pack settings detected, updating config...");
                CONFIG.setHash(config.getHash());
                CONFIG.mergeResources(config);
                CONFIG.getGlobal().setActiveAll(false);
                CONFIG.save();
            }
            resourceConfig = config;
        });
    }

    private static Optional<CursorConfig.Resource> getLayeredSettings(List<Resource> configResources) {
        CursorConfig.Resource layeredResources = null;
        for (Resource configResource : configResources) {
            try (InputStream stream = configResource.open()) {
                CursorConfig.Resource resourceConfig = CursorConfigLoader.loadResource(stream);
                if (layeredResources == null) {
                    layeredResources = resourceConfig;
                } else {
                    layeredResources.layer(resourceConfig.getSettings());
                }
            } catch (IOException e) {
                LOGGER.error("[minecraft-cursor] Failed to load settings of resource pack '{}'", configResource.sourcePackId());
            }
        }
        return Optional.ofNullable(layeredResources);
    }

    public static void applyResourceSettings() {
        if (resourceConfig != null) {
            CONFIG.setHash(resourceConfig.getHash());
            CONFIG.layerResources(resourceConfig);
            for (Cursor cursor : CursorManager.INSTANCE.getCursors()) {
                cursor.applySettings(CONFIG.getOrCreateCursorSettings(cursor));
            }
        } else {
            LOGGER.error("Failed to apply resource config: Not Found.");
        }
    }

    private static void loadCursorTextures(ResourceManager manager) {
        for (Cursor cursor : CursorManager.INSTANCE.getCursors()) {
            CursorConfig.Settings settings = CONFIG.getOrCreateCursorSettings(cursor);
            loadCursorTexture(manager, cursor, settings);
        }
    }

    public static boolean loadCursorTexture(Cursor cursor) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!loadCursorTexture(minecraft.getResourceManager(), cursor, CONFIG.getOrCreateCursorSettings(cursor))) {
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
        LOGGER.info("[minecraft-cursor] Loading cursor '{}'...", cursor.getTypeKey());

        ResourceLocation location = cursor.getLocation();
        Optional<Resource> cursorResource = manager.getResource(location);

        if (cursorResource.isEmpty()) {
            LOGGER.error("[minecraft-cursor] Cursor Type: '{}' not found", cursor.getTypeKey());
            return false;
        }

        try (InputStream cursorStream = cursorResource.get().open(); NativeImage image = NativeImage.read(cursorStream)) {
            AnimatedCursorConfig animation = loadAnimation(manager, location, cursorResource.get());
            Cursor loaded = CursorManager.INSTANCE.loadCursor(cursor, image, CONFIG.getGlobal().apply(settings), animation);
            settings.update(loaded, loaded.getScale(), loaded.getXHot(), loaded.getYHot(), loaded.isEnabled());

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
}
