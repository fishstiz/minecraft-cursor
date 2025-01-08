package io.github.fishstiz.minecraftcursor;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import java.util.Optional;

class MinecraftCursorResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    private static final String PATH = "cursor.png";
    private final MinecraftCursorHandler cursorHandler = new MinecraftCursorHandler();

    @Override
    public Identifier getFabricId() {
        return Identifier.of(MinecraftCursor.MOD_ID, PATH);
    }

    @Override
    public void reload(ResourceManager manager) {
        Optional<Resource> cursorOptionalResource = manager.getResource(this.getFabricId());

        if (cursorOptionalResource.isEmpty()) {
            return;
        }

        try (InputStream cursorStream = cursorOptionalResource.get().getInputStream()) {
            BufferedImage cursorImage = ImageIO.read(cursorStream);
            cursorHandler.setCursor(cursorImage, 1, 0,0);
            cursorImage.flush();
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Error occurred while loading cursor: {}", String.valueOf(e));
        }
    }
}