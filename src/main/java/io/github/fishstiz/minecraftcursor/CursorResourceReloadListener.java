package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.AnimatedCursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfigLoader;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class CursorResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    private static final String IMG_TYPE = ".png";
    private static final String IMG_CONFIG_TYPE = IMG_TYPE + ".json";
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
        Optional<Resource> resourceOptional = manager.getResource(Identifier.of(namespace, CONFIG_PATH));

        if (resourceOptional.isEmpty()) return;

        try (InputStream stream = resourceOptional.get().getInputStream()) {
            CursorConfig resourceConfig = CursorConfigLoader.fromStream(stream);
            if (!resourceConfig.get_hash().equals(config.get_hash())) {
                config.set_hash(resourceConfig.get_hash());
                config.setSettings(resourceConfig.getSettings());
                config.save();
                MinecraftCursor.LOGGER.info("Using cursor settings provided by resource pack");
            }
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Failed to load resource pack's cursor settings");
        }
    }

    private void loadCursorTextures(ResourceManager manager) {
        Map<Identifier, Resource> cursorResources = manager.findResources(
                this.getFabricId().getPath(),
                id -> id.getPath().endsWith(IMG_TYPE) && getCursorTypeByIdentifierOrNull(id) != null
        );
        Map<String, AnimatedCursorConfig> animationConfigs =getAnimationConfigs(manager);

        for (Map.Entry<Identifier, Resource> entry : cursorResources.entrySet()) {
            CursorType cursorType = getCursorTypeByIdentifierOrNull(entry.getKey());
            assert cursorType != null;

            BufferedImage image = null;
            try (InputStream stream = entry.getValue().getInputStream()) {
                image = ImageIO.read(stream);

                if (image == null) {
                    MinecraftCursor.LOGGER.error("Invalid file for {}. Supported types: {}", cursorType, IMG_TYPE);
                    continue;
                }

                cursorManager.loadCursorImage(
                        cursorType,
                        entry.getKey(), // Identifier
                        image,
                        config.getOrCreateCursorSettings(cursorType),
                        animationConfigs.get(cursorType.getKey())
                );
            } catch (IOException e) {
                MinecraftCursor.LOGGER.error("Failed to load cursor image {}", entry.getKey().getPath());
            } finally {
                if (image != null) {
                    image.flush();
                }
            }
        }
    }

    private Map<String, AnimatedCursorConfig> getAnimationConfigs(ResourceManager manager) {
        Map<String, AnimatedCursorConfig> animationConfigs = new HashMap<>();

        Map<Identifier, Resource> configResources = manager.findResources(
                this.getFabricId().getPath(),
                id -> id.getPath().endsWith(IMG_CONFIG_TYPE) && getCursorTypeByIdentifierOrNull(id) != null
        );

        for (Map.Entry<Identifier, Resource> entry : configResources.entrySet()) {
            CursorType cursorType = getCursorTypeByIdentifierOrNull(entry.getKey());
            assert cursorType != null;

            try (InputStream stream = entry.getValue().getInputStream()) {
                animationConfigs.put(cursorType.getKey(), CursorConfigLoader.getAnimationConfig(stream));
            } catch (IOException e) {
                MinecraftCursor.LOGGER.error("Failed to load animation config for {}", cursorType.getKey());
            }
        }

        return animationConfigs;
    }

    private static @Nullable CursorType getCursorTypeByIdentifierOrNull(Identifier id) {
        String[] path = id.getPath().split("/");
        String name = path[path.length - 1].split(IMG_TYPE)[0];

        return CursorTypeRegistry.getCursorTypeOrNull(name);
    }
}
