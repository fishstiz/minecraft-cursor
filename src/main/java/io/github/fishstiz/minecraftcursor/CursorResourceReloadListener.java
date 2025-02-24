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

class CursorResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    private static final String IMG_TYPE = ".png";
    private static final String ANIMATION_TYPE = IMG_TYPE + ".mcmeta";
    private static final String CONFIG_PATH = "atlases/cursors.json";
    private static final String CURSORS_DIR = "textures/cursors";
    private final CursorManager cursorManager;
    private final CursorConfig config;
    private final String namespace;

    CursorResourceReloadListener(CursorManager cursorManager, String namespace, CursorConfig config) {
        this.cursorManager = cursorManager;
        this.namespace = namespace;
        this.config = config;
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(namespace, CURSORS_DIR);
    }

    @Override
    public void reload(ResourceManager manager) {
        loadConfig(manager);
        loadCursorTextures(manager);
        cursorManager.setCurrentCursor(CursorType.DEFAULT);
    }

    private void loadConfig(ResourceManager manager) {
        Resource configResource = manager.getResource(Identifier.of(namespace, CONFIG_PATH)).orElse(null);

        if (configResource == null) return;

        try (InputStream stream = configResource.getInputStream()) {
            CursorConfig resourceConfig = CursorConfigLoader.fromStream(stream);
            if (!resourceConfig.get_hash().equals(config.get_hash())) {
                config.set_hash(resourceConfig.get_hash());
                config.setSettings(resourceConfig.getSettings());
                config.save();
                MinecraftCursor.LOGGER.info("New resource pack settings detected for minecraft-cursor '{}'", configResource.getResourcePackName());
            }
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Failed to load settings of resource pack '{}'", configResource.getResourcePackName());
        }
    }

    private void loadCursorTextures(ResourceManager manager) {
        for (CursorType cursorType : cursorManager.getCursorTypes()) {
            String basePath = CURSORS_DIR + "/" + cursorType.getKey();
            loadCursorTexture(manager, cursorType, basePath);
        }
    }

    private void loadCursorTexture(ResourceManager manager, CursorType cursorType, String basePath) {
        Identifier cursorId = Identifier.of(namespace, basePath + IMG_TYPE);
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
            cursorManager.loadCursorImage(cursorType, cursorId, image, config.getOrCreateCursorSettings(cursorType), animation);
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Failed to load cursor image for '{}'", basePath);
        } finally {
            if (image != null) image.flush();
        }
    }

    private AnimatedCursorConfig loadAnimationConfig(ResourceManager manager, String basePath, Resource cursorResource) {
        Resource animationResource = manager
                .getResource(Identifier.of(namespace, basePath + ANIMATION_TYPE))
                .orElse(null);

        if (animationResource != null && animationResource.getResourcePackName().equals(cursorResource.getResourcePackName())) {
            try (InputStream stream = animationResource.getInputStream()) {
                return CursorConfigLoader.getAnimationConfig(stream);
            } catch (IOException e) {
                MinecraftCursor.LOGGER.error("Failed to load animation config for '{}'", basePath);
            }
        }

        return null;
    }
}
