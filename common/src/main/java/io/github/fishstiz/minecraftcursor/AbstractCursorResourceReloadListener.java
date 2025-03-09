package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.AnimatedCursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfigLoader;
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

abstract class AbstractCursorResourceReloadListener {
    private static final String IMG_TYPE = ".png";
    private static final String ANIMATION_TYPE = IMG_TYPE + ".mcmeta";
    private static final String CONFIG_PATH = "atlases/cursors.json";
    private static final String CURSORS_DIR = "textures/cursors/";

    protected AbstractCursorResourceReloadListener() {
    }

    public ResourceLocation getId() {
        return ResourceLocation.tryBuild(MOD_ID, CURSORS_DIR);
    }

    public void reloadMinecraftCursor(ResourceManager manager) {
        loadConfig(manager);
        loadCursorTextures(manager);
        CursorManager.INSTANCE.setCurrentCursor(CursorType.DEFAULT);
    }

    private void loadConfig(ResourceManager manager) {
        List<Resource> configResources = manager.getResourceStack(ResourceLocation.tryBuild(MOD_ID, CONFIG_PATH));

        if (configResources.isEmpty()) return;

        getConfigFromResources(configResources).ifPresent(config -> {
            if (!config.get_hash().equals(CONFIG.get_hash())) {
                CONFIG.set_hash(config.get_hash());
                CONFIG.setSettings(config.getSettings());
                CONFIG.save();
                MinecraftCursor.LOGGER.info("New resource pack settings detected for minecraft-cursor");
            }
        });
    }

    private Optional<CursorConfig> getConfigFromResources(List<Resource> configResources) {
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
                MinecraftCursor.LOGGER.error("Failed to load settings of resource pack '{}'", configResource.sourcePackId());
            }
        }

        return Optional.ofNullable(combinedConfig);
    }

    private void loadCursorTextures(ResourceManager manager) {
        for (CursorType cursorType : CursorManager.INSTANCE.getCursorTypes()) {
            String basePath = CURSORS_DIR + cursorType.getKey();
            loadCursorTexture(manager, cursorType, basePath);
        }
    }

    private void loadCursorTexture(ResourceManager manager, CursorType cursorType, String basePath) {
        ResourceLocation cursorId = ResourceLocation.tryBuild(MOD_ID, basePath + IMG_TYPE);
        Resource cursorResource = manager.getResource(cursorId).orElse(null);


        if (cursorResource == null) {
            MinecraftCursor.LOGGER.error("Cursor Type: '{}' not found", cursorType.getKey());
            return;
        }

        BufferedImage image = null;
        try (InputStream cursorStream = cursorResource.open()) {
            image = ImageIO.read(cursorStream);
            if (image == null) {
                MinecraftCursor.LOGGER.error("Invalid file for cursor type '{}'", cursorType);
                return;
            }

            AnimatedCursorConfig animation = loadAnimationConfig(manager, basePath, cursorResource);
            CursorManager.INSTANCE.loadCursorImage(cursorType, cursorId, image, CONFIG.getOrCreateCursorSettings(cursorType), animation);
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Failed to load cursor image for '{}'", basePath);
        } finally {
            if (image != null) image.flush();
        }
    }

    private AnimatedCursorConfig loadAnimationConfig(ResourceManager manager, String basePath, Resource cursorResource) {
        Resource animationResource = manager
                .getResource(ResourceLocation.tryBuild(MOD_ID, basePath + ANIMATION_TYPE))
                .orElse(null);

        if (animationResource != null && animationResource.sourcePackId().equals(cursorResource.sourcePackId())) {
            try (InputStream stream = animationResource.open()) {
                return CursorConfigLoader.getAnimationConfig(stream);
            } catch (IOException e) {
                MinecraftCursor.LOGGER.error("Failed to load animation config for '{}'", basePath);
            }
        }

        return null;
    }
}
