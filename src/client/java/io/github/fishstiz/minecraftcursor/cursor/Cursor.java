package io.github.fishstiz.minecraftcursor.cursor;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.utils.BufferedImageUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class Cursor {
    private final CursorType type;
    private String base64Image;
    private long id = 0;
    private double scale;
    private int xhot;
    private int yhot;
    private boolean enabled;

    public Cursor(CursorType type) {
        this.type = type;
    }

    public void updateImage(double scale, int xhot, int yhot) {
        if (id == 0) {
            return;
        }

        try {
            BufferedImage image = BufferedImageUtils.decompressBase64ToImage(base64Image);
            create(image, scale, xhot, yhot);
            image.flush();
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Error updating image of {}: {}", type, e);
        }
    }

    public void loadImage(BufferedImage image, double scale, int xhot, int yhot, boolean enabled) throws IOException {
        this.base64Image = BufferedImageUtils.compressImageToBase64(image);
        this.enabled = enabled;

        create(image, scale, xhot, yhot);
    }

    private void create(BufferedImage image, double scale, int xhot, int yhot) {
        BufferedImage scaledImage = scale == 1 ? image : BufferedImageUtils.scaleImage(image, scale);
        int scaledXHot = scale == 1 ? xhot : (int) Math.round(xhot * scale);
        int scaledYHot = scale == 1 ? yhot : (int) Math.round(yhot * scale);

        @SuppressWarnings("resource")
        GLFWImage.Buffer glfwImageBuffer = GLFWImage.create(1);
        glfwImageBuffer.width(scaledImage.getWidth());
        glfwImageBuffer.height(scaledImage.getHeight());
        glfwImageBuffer.pixels(BufferedImageUtils.getPixelsRGBA(image));
        scaledImage.flush();

        long id = GLFW.glfwCreateCursor(glfwImageBuffer.get(), scaledXHot, scaledYHot);

        if (this.id != 0 && this.id != id) {
            destroy();
        }

        this.id = id;
        this.scale = scale;
        this.xhot = xhot;
        this.yhot = yhot;
    }

    public void destroy() {
        if (id != 0) {
            GLFW.glfwDestroyCursor(id);
            id = 0;
        }
    }

    public void enable(boolean enabled) {
        if (enabled) {
            enable();
        } else {
            disable();
        }
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

    public int getXhot() {
        return xhot;
    }

    public int getYhot() {
        return yhot;
    }

    public boolean getEnabled() {
        return enabled;
    }
}
