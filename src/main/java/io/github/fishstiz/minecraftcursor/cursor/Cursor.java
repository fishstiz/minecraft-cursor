package io.github.fishstiz.minecraftcursor.cursor;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.util.BufferedImageUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Consumer;

public class Cursor {
    protected static final int SIZE = 32;
    protected final Consumer<Cursor> onLoad;
    private final CursorType type;
    private Identifier sprite;
    private String cachedBufferedImage;
    private double scale;
    private int xhot;
    private int yhot;
    private boolean enabled;
    private boolean loaded;
    private long id = 0;

    public Cursor(CursorType type, Consumer<Cursor> onLoad) {
        this.type = type;
        this.onLoad = onLoad;
    }

    public void loadImage(Identifier sprite, BufferedImage image, CursorConfig.Settings settings) throws IOException {
        BufferedImage croppedImage = image;
        if (image.getWidth() > SIZE || image.getHeight() > SIZE) {
            croppedImage = BufferedImageUtil.cropImage(croppedImage, new Rectangle(SIZE, SIZE));
        }

        this.sprite = sprite;
        this.cachedBufferedImage = BufferedImageUtil.compressImageToBase64(croppedImage);
        this.enabled = settings.isEnabled();

        create(croppedImage, settings.getScale(), settings.getXHot(), settings.getYHot());
        croppedImage.flush();
    }

    protected void updateImage(double scale, int xhot, int yhot) {
        if (id == 0) {
            return;
        }

        try {
            BufferedImage image = BufferedImageUtil.decompressBase64ToImage(cachedBufferedImage);
            create(image, scale, xhot, yhot);
            image.flush();
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Error updating image of {}: {}", type, e);
        }
    }

    private void create(BufferedImage image, double scale, int xhot, int yhot) {
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

        if (onLoad != null) {
            onLoad.accept(this);
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

    public void setScale(double scale) {
        updateImage(scale, getXhot(), getYhot());
    }

    public int getXhot() {
        return xhot;
    }

    public void setXhot(int xhot) {
        updateImage(getScale(), xhot, getYhot());
    }

    public int getYhot() {
        return yhot;
    }

    public void setYhot(int yhot) {
        updateImage(getScale(), getXhot(), yhot);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isLoaded() {
        return loaded;
    }
}
