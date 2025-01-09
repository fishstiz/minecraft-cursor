package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.config.MinecraftCursorConfigManager;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import java.util.Optional;

public class MinecraftCursorResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    public static final String PATH = "textures/cursor.png";
    private final MinecraftCursorHandler cursorHandler;
    private final MinecraftCursorConfigManager configManager;

    public MinecraftCursorResourceReloadListener(MinecraftCursorHandler cursorHandler, MinecraftCursorConfigManager configManager) {
        this.configManager = configManager;
        this.cursorHandler = cursorHandler;
    }

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
            cursorHandler.loadImage(cursorImage, configManager.getScale(), configManager.getXhot(),configManager.getYhot(), configManager.getEnabled());
            cursorImage.flush();
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Error occurred while loading cursor: {}", String.valueOf(e));
        }
    }
}