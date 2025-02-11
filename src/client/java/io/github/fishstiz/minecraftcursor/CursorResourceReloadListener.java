package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
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
import java.util.Map;
import java.util.Optional;

class CursorResourceReloadListener implements SimpleSynchronousResourceReloadListener {
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
        for (Map.Entry<Identifier, Resource> entry : manager.findResources(
                getFabricId().getPath(),
                id -> getCursorTypeByIdentifierOrNull(id) != null).entrySet()
        ) {
            CursorType cursorType = getCursorTypeByIdentifierOrNull(entry.getKey());
            assert cursorType != null;

            BufferedImage image = null;
            try (InputStream stream = entry.getValue().getInputStream()) {
                image = ImageIO.read(stream);
                cursorManager.loadCursorImage(
                        cursorType,
                        entry.getKey(), // Identifier
                        image,
                        config.getOrCreateCursorSettings(cursorType)
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

    private static @Nullable CursorType getCursorTypeByIdentifierOrNull(Identifier id) {
        String fileType = ".png";
        String[] path = id.getPath().split("/");
        String name = path[path.length - 1].split(fileType)[0];

        return CursorTypeRegistry.getCursorTypeOrNull(name);
    }
}
