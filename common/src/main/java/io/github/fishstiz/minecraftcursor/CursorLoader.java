package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.AnimatedCursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfigLoader;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;
import static io.github.fishstiz.minecraftcursor.MinecraftCursor.MOD_ID;

public class CursorLoader {
    private static final String IMG_TYPE = ".png";
    private static final String ANIMATION_TYPE = IMG_TYPE + ".mcmeta";
    private static final String CONFIG_PATH = "atlases/cursors.json";
    private static final String CURSORS_DIR = "textures/cursors/";

    private CursorLoader() {
    }

    public static ResourceLocation getLocation() {
        return new ResourceLocation(MOD_ID, CURSORS_DIR);
    }

    public static void reload(ResourceManager manager) {
        CursorManager.INSTANCE.setCurrentCursor(CursorType.DEFAULT);
        loadConfig(manager);
        loadCursorTextures(manager);
        CursorManager.INSTANCE.setCurrentCursor(CursorType.DEFAULT);
    }

    private static void loadConfig(ResourceManager manager) {
        List<Resource> configResources = manager.getResourceStack(new ResourceLocation(MOD_ID, CONFIG_PATH));

        if (configResources.isEmpty()) return;

        getConfigFromResources(configResources).ifPresent(config -> {
            if (!config.get_hash().equals(CONFIG.get_hash())) {
                MinecraftCursor.LOGGER.info("[minecraft-cursor] New resource pack settings detected, updating config...");
                CONFIG.set_hash(config.get_hash());
                CONFIG.setSettings(config.getSettings());
                CONFIG.getGlobal().setActiveAll(false);
                CONFIG.save();
            }
        });
    }

    private static Optional<CursorConfig> getConfigFromResources(List<Resource> configResources) {
        CursorConfig combinedConfig = null;

        for (Resource configResource : configResources) {
            try (InputStream stream = configResource.open()) {
                CursorConfig loadedConfig = CursorConfigLoader.fromStream(stream);

                if (combinedConfig == null) {
                    combinedConfig = loadedConfig;
                } else {
                    combinedConfig.setSettings(loadedConfig.getSettings());
                }
            } catch (IOException e) {
                MinecraftCursor.LOGGER.error("[minecraft-cursor] Failed to load settings of resource pack '{}'", configResource.sourcePackId());
            }
        }

        return Optional.ofNullable(combinedConfig);
    }

    private static void loadCursorTextures(ResourceManager manager) {
        for (Cursor cursor : CursorManager.INSTANCE.getCursors()) {
            String basePath = CURSORS_DIR + cursor.getType().getKey();
            loadCursorTexture(manager, cursor, basePath);
        }
    }

    private static void loadCursorTexture(ResourceManager manager, Cursor cursor, String basePath) {
        ResourceLocation location = new ResourceLocation(MOD_ID, basePath + IMG_TYPE);
        Resource cursorResource = manager.getResource(location).orElse(null);

        if (cursorResource == null) {
            MinecraftCursor.LOGGER.error("[minecraft-cursor] Cursor Type: '{}' not found", cursor.getType().getKey());
            return;
        }

        BufferedImage image = null;
        try (InputStream cursorStream = cursorResource.open()) {
            image = ImageIO.read(cursorStream);
            if (image == null) {
                MinecraftCursor.LOGGER.error("[minecraft-cursor] Invalid file for cursor type '{}'", cursor.getType().getKey());
                return;
            }

            AnimatedCursorConfig animation = loadAnimation(manager, basePath, cursorResource);
            CursorManager.INSTANCE.loadCursor(cursor, location, image, animation);
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("[minecraft-cursor] Failed to load cursor image for '{}'", basePath);
        } finally {
            if (image != null) image.flush();
        }
    }

    private static AnimatedCursorConfig loadAnimation(ResourceManager manager, String basePath, Resource cursorResource) {
        Resource animationResource = manager
                .getResource(new ResourceLocation(MOD_ID, basePath + ANIMATION_TYPE))
                .orElse(null);

        if (animationResource != null && animationResource.sourcePackId().equals(cursorResource.sourcePackId())) {
            try (InputStream stream = animationResource.open()) {
                return CursorConfigLoader.getAnimationConfig(stream);
            } catch (IOException e) {
                MinecraftCursor.LOGGER.error("[minecraft-cursor] Failed to load animation config for '{}'", basePath);
            }
        }

        return null;
    }
}
