package io.github.fishstiz.minecraftcursor.cursor;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.util.BufferedImageUtil;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class Cursor {
    private final CursorType type;
    private Identifier sprite;
    private String base64Image;
    private long id = 0;
    private double scale;
    private int xhot;
    private int yhot;
    private boolean enabled;
    private boolean loaded;

    public Cursor(CursorType type) {
        this.type = type;
    }

    public void loadImage(Identifier sprite, BufferedImage image, double scale, int xhot, int yhot, boolean enabled) throws IOException {
        this.sprite = sprite;
        this.base64Image = BufferedImageUtil.compressImageToBase64(image);
        this.enabled = enabled;

        create(image, scale, xhot, yhot, null);
    }

    private void updateImage(double scale, int xhot, int yhot, Runnable onUpdate) {
        if (id == 0) {
            return;
        }

        try {
            BufferedImage image = BufferedImageUtil.decompressBase64ToImage(base64Image);
            create(image, scale, xhot, yhot, onUpdate);
            image.flush();
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Error updating image of {}: {}", type, e);
        }
    }

    private void create(BufferedImage image, double scale, int xhot, int yhot, @Nullable Runnable onCreate) {
        BufferedImage scaledImage = scale == 1 ? image : BufferedImageUtil.scaleImage(image, scale);
        int scaledXHot = scale == 1 ? xhot : (int) Math.round(xhot * scale);
        int scaledYHot = scale == 1 ? yhot : (int) Math.round(yhot * scale);

        @SuppressWarnings("resource")
        GLFWImage.Buffer glfwImageBuffer = GLFWImage.create(1);
        glfwImageBuffer.width(scaledImage.getWidth());
        glfwImageBuffer.height(scaledImage.getHeight());
        glfwImageBuffer.pixels(BufferedImageUtil.getPixelsRGBA(scaledImage));
        scaledImage.flush();

        long previousId = this.id;
        this.id = GLFW.glfwCreateCursor(glfwImageBuffer.get(), scaledXHot, scaledYHot);

        if (onCreate != null) {
            onCreate.run();
        }

        if (previousId != 0 && this.id != previousId) {
            GLFW.glfwDestroyCursor(previousId);
        }

        loaded = true;
        this.scale = scale;
        this.xhot = xhot;
        this.yhot = yhot;
    }

    public void enable(boolean enabled) {
        if (enabled) {
            enable();
        } else {
            disable();
        }
    }

    public Identifier getSprite() {
        return sprite;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public long getId() {
        return enabled ? id : 0;
    }

    public CursorType getType() {
        return type;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale, @Nullable Runnable onUpdate) {
        updateImage(scale, getXhot(), getYhot(), onUpdate);
    }

    public int getXhot() {
        return xhot;
    }

    public void setXhot(int xhot, @Nullable Runnable onUpdate) {
        updateImage(getScale(), xhot, getYhot(), onUpdate);
    }

    public int getYhot() {
        return yhot;
    }

    public void setYhot(int yhot, @Nullable Runnable onUpdate) {
        updateImage(getScale(), getXhot(), yhot, onUpdate);
    }

    public boolean getEnabled() {
        return enabled;
    }

    public boolean isLoaded() {
        return loaded;
    }
}
