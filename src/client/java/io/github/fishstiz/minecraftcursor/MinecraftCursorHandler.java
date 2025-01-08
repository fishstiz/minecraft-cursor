package io.github.fishstiz.minecraftcursor;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

class MinecraftCursorHandler {
    private long cursor;

    public void setCursor(BufferedImage image, double scale, int xhot, int yhot) {
        if (image == null) {
            return;
        }

        long previousCursor = 0;
        if (this.cursor != 0) {
            previousCursor = this.cursor;
        }

        BufferedImage scaledImage = scaleImage(image, scale);
        image.flush();

        ByteBuffer pixels = getPixelsRGBA(scaledImage);
        scaledImage.flush();

        GLFWImage.Buffer glfwImageBuffer = GLFWImage.create(1);
        glfwImageBuffer.width(scaledImage.getWidth());
        glfwImageBuffer.height(scaledImage.getHeight());
        glfwImageBuffer.pixels(pixels);

        this.cursor = GLFW.glfwCreateCursor(glfwImageBuffer.get(), xhot, yhot);
        GLFW.glfwSetCursor(MinecraftClient.getInstance().getWindow().getHandle(), this.cursor);

        glfwImageBuffer.clear();

        if (previousCursor != 0 && previousCursor != this.cursor) {
            GLFW.glfwDestroyCursor(previousCursor);
        }
    }

    private BufferedImage scaleImage(BufferedImage image, double scale) {
        int width = (int) Math.ceil(image.getWidth() * scale);
        int height = (int) Math.ceil(image.getHeight() * scale);

        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return scaledImage;
    }

    private ByteBuffer getPixelsRGBA(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));  // red
                buffer.put((byte) ((pixel >> 8) & 0xFF));   // green
                buffer.put((byte) (pixel & 0xFF));          // blue
                buffer.put((byte) ((pixel >> 24) & 0xFF));  // alpha
            }
        }

        buffer.flip();
        return buffer;
    }
}
