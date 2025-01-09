package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.utils.BufferedImageUtils;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MinecraftCursorHandler {
    private final MinecraftClient client;
    private long cursor = 0;
    private String base64Image;
    private boolean isEnabled;
    private int width = 0;
    private int height = 0;

    public MinecraftCursorHandler(MinecraftClient client) {
        this.client = client;
    }

    protected void loadImage(BufferedImage image, double scale, int xhot, int yhot, boolean set) {
        if (image == null) {
            return;
        }

        this.isEnabled = set;

        try {
            this.width = image.getWidth();
            this.height = image.getHeight();
            this.base64Image = BufferedImageUtils.compressImageToBase64(image);

            long previousCursor = 0;
            if (this.cursor != 0) {
                previousCursor = this.cursor;
            }


            this.createCursor(image, scale, xhot, yhot);

            if (previousCursor != 0 && previousCursor != this.cursor) {
                GLFW.glfwDestroyCursor(previousCursor);
            }
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Error loading cursor: {}", String.valueOf(e));
        } finally {
            image.flush();
        }
    }

    private void createCursor(BufferedImage image, double scale, int xhot, int yhot) {
        BufferedImage scaledImage = BufferedImageUtils.scaleImage(image, scale);
        image.flush();
        int scaledXHot = (int) Math.round(xhot * scale);
        int scaledYHot = (int) Math.round(yhot * scale);

        ByteBuffer pixels = BufferedImageUtils.getPixelsRGBA(scaledImage);
        scaledImage.flush();

        @SuppressWarnings("resource")
        GLFWImage.Buffer glfwImageBuffer = GLFWImage.create(1);
        glfwImageBuffer.width(scaledImage.getWidth());
        glfwImageBuffer.height(scaledImage.getHeight());
        glfwImageBuffer.pixels(pixels);

        this.cursor = GLFW.glfwCreateCursor(glfwImageBuffer.get(), scaledXHot, scaledYHot);

        if (this.isEnabled) {
            this.setCursor(this.cursor);
        }

        glfwImageBuffer.clear();
    }

    private void setCursor(long cursor) {
        GLFW.glfwSetCursor(this.client.getWindow().getHandle(), cursor);
    }

    public void updateCursor(double scale, int xhot, int yhot) {
        if (this.base64Image == null || !this.isEnabled) {
            return;
        }

        try {
            BufferedImage image = BufferedImageUtils.decompressBase64ToImage(base64Image);
            this.createCursor(image, scale, xhot, yhot);
            image.flush();
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Error updating cursor: {}", String.valueOf(e));
        }
    }

    public void enable(boolean isEnabled) {
        if (isEnabled) {
            enable();
        } else {
            disable();
        }
    }

    public void enable() {
        if (this.cursor == 0) {
            return;
        }

        this.setCursor(this.cursor);
        this.isEnabled = true;
    }

    public void disable() {
        if (this.cursor == 0) {
            return;
        }

        this.setCursor(0);
        this.isEnabled = false;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
