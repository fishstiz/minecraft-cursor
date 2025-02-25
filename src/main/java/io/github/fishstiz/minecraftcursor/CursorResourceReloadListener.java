package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.AnimatedCursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfigLoader;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;
import static io.github.fishstiz.minecraftcursor.MinecraftCursor.MOD_ID;

class CursorResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    public static final CursorResourceReloadListener INSTANCE = new CursorResourceReloadListener();
    private static final String IMG_TYPE = ".png";
    private static final String ANIMATION_TYPE = IMG_TYPE + ".mcmeta";
    private static final String CONFIG_PATH = "atlases/cursors.json";
    private static final String CURSORS_DIR = "textures/cursors/";

    private CursorResourceReloadListener() {
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(MOD_ID, CURSORS_DIR);
    }

    @Override
    public void reload(ResourceManager manager) {
        loadConfig(manager);
        loadCursorTextures(manager);
        CursorManager.INSTANCE.setCurrentCursor(CursorType.DEFAULT);
    }

    private void loadConfig(ResourceManager manager) {
        List<Resource> configResources = manager.getAllResources(Identifier.of(MOD_ID, CONFIG_PATH));

        if (configResources == null || configResources.isEmpty()) return;

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
            try (InputStream stream = configResource.getInputStream()) {
                CursorConfig loadedConfig = CursorConfigLoader.fromStream(stream);

                if (combinedConfig == null) {
                    combinedConfig = loadedConfig;
                } else {
                    combinedConfig.setSettings(loadedConfig.getSettings());
                }
            } catch (IOException e) {
                MinecraftCursor.LOGGER.error("Failed to load settings of resource pack '{}'", configResource.getPackId());
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
        Identifier cursorId = Identifier.of(MOD_ID, basePath + IMG_TYPE);
        Resource cursorResource = manager.getResource(cursorId).orElse(null);

        if (cursorResource == null) return;

        BufferedImage image = null;
        try (InputStream cursorStream = cursorResource.getInputStream()) {
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
                .getResource(Identifier.of(MOD_ID, basePath + ANIMATION_TYPE))
                .orElse(null);

        if (animationResource != null && animationResource.getPackId().equals(cursorResource.getPackId())) {
            try (InputStream stream = animationResource.getInputStream()) {
                return CursorConfigLoader.getAnimationConfig(stream);
            } catch (IOException e) {
                MinecraftCursor.LOGGER.error("Failed to load animation config for '{}'", basePath);
            }
        }

        return null;
    }
}
