package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
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

public class CursorResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    private static final String PATH = "textures/cursors";
    private static final String FILE_EXTENSION = ".png";
    private final CursorManager cursorManager;
    private final String modId;

    CursorResourceReloadListener(CursorManager cursorManager, String modId) {
        this.cursorManager = cursorManager;
        this.modId = modId;
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(modId, PATH);
    }

    @Override
    public void reload(ResourceManager manager) {
        for (Map.Entry<Identifier, Resource> entry : manager.findResources(getFabricId().getPath(), id -> getCursorTypeByIdentifierOrNull(id) != null).entrySet()) {
            CursorType cursorType = getCursorTypeByIdentifierOrNull(entry.getKey());

            BufferedImage image = null;
            try (InputStream stream = entry.getValue().getInputStream()) {
                image = ImageIO.read(stream);
                cursorManager.loadCursorImage(cursorType, image);
            } catch (IOException e) {
                MinecraftCursor.LOGGER.error("Failed to load {}: {}", entry.getKey().getPath(), e.toString());
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

        return CursorType.getCursorTypeOrNull(name);
    }
}
