package io.github.fishstiz.minecraftcursor.util;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;

public class BufferedImageUtil {
    private BufferedImageUtil() {
    }

    public static ByteBuffer getPixelsRGBA(BufferedImage image) {
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

    public static BufferedImage scaleImage(BufferedImage image, double scale) {
        int width = (int) Math.round(image.getWidth() * scale);
        int height = (int) Math.round(image.getHeight() * scale);

        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return scaledImage;
    }

    public static String compressImageToBase64(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            byte[] imageData = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageData);
        }
    }

    public static BufferedImage cropImage(BufferedImage src, Rectangle rect) {
        return src.getSubimage(rect.x, rect.y, rect.width, rect.height);
    }

    public static BufferedImage decompressBase64ToImage(String base64Image) throws IOException {
        byte[] imageData = Base64.getDecoder().decode(base64Image);

        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData)) {
            return ImageIO.read(bais);
        }
    }
}
