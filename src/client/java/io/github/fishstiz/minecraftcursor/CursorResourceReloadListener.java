package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfigLoader;
import io.github.fishstiz.minecraftcursor.config.CursorConfigService;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.cursor.CursorTypeRegistry;
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

public class CursorResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    private static final String PATH = "textures/cursors";
    private static final String FILE_EXTENSION = ".png";
    private static final String CONFIG_PATH = "atlases/cursors.json";
    private final CursorManager cursorManager;
    private final String modId;
    private final CursorConfig userConfig;
    private final CursorConfig config;

    CursorResourceReloadListener(CursorManager cursorManager, String modId, CursorConfigService userConfig) {
        this.cursorManager = cursorManager;
        this.modId = modId;
        this.userConfig = userConfig.get();
        config = userConfig.get();
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(modId, PATH);
    }

    @Override
    public void reload(ResourceManager manager) {
        initConfig(manager);
        loadCursorTextures(manager);
    }

    private void initConfig(ResourceManager manager) {
        Optional<Resource> resourceConfigResourceOpt = manager.getResource(Identifier.of(modId, CONFIG_PATH));

        if (resourceConfigResourceOpt.isEmpty()) {
            return;
        }

        try (InputStream stream = resourceConfigResourceOpt.get().getInputStream()) {
            CursorConfig resourceConfig = new CursorConfigLoader(stream).config();
            if (!resourceConfig.get_hash().equals(userConfig.get_hash())) {
                userConfig.set_hash(resourceConfig.get_hash());
                config.setSettings(resourceConfig.getSettings());
                MinecraftCursor.LOGGER.info("Using default cursor settings provided by resource");
            }
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Failed to load resource cursor settings", e);
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
                cursorManager.loadCursorImage(cursorType, entry.getKey(), image, config.getOrCreateCursorSettings(cursorType));
            } catch (IOException e) {
                MinecraftCursor.LOGGER.error("Failed to load image {}", entry.getKey().getPath(), e);
            } finally {
                if (image != null) {
                    image.flush();
                }
            }
        }
    }

    @Nullable
    private CursorType getCursorTypeByIdentifierOrNull(Identifier id) {
        String[] path = id.getPath().split("/");
        String name = path[path.length - 1].split(FILE_EXTENSION)[0];

        return CursorTypeRegistry.getCursorTypeOrNull(name);
    }
}
