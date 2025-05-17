package io.github.fishstiz.minecraftcursor.util;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.mixin.NativeImageAccess;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Base64;

public class NativeImageUtil {
    private NativeImageUtil() {
    }

    public static NativeImage cropImage(NativeImage src, int xOffset, int yOffset, int width, int height) {
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        if (xOffset < 0 || yOffset < 0 || xOffset + width > srcWidth || yOffset + height > srcHeight) {
            MinecraftCursor.LOGGER.error(
                    "[minecraft-cursor] Image size {}x{} is invalid. Valid size: {}x{} at y {}",
                    srcWidth, srcHeight, width, height, yOffset
            );
            return src;
        }

        NativeImage out = new NativeImage(width, height, true);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = src.getPixelRGBA(x + xOffset, y + yOffset);
                out.setPixelRGBA(x, y, color);
            }
        }

        return out;
    }

    public static NativeImage scaleImage(NativeImage src, double scale) {
        int width = (int) Math.round(src.getWidth() * scale);
        int height = (int) Math.round(src.getHeight() * scale);
        NativeImage scaled = new NativeImage(width, height, true);

        for (int y = 0; y < height; y++) { // nearest neighbor
            for (int x = 0; x < width; x++) {
                int srcX = (int) (x / scale);
                int srcY = (int) (y / scale);
                scaled.setPixelRGBA(x, y, src.getPixelRGBA(srcX, srcY));
            }
        }
        return scaled;
    }

    public static void writePixelsRGBA(NativeImage image, ByteBuffer buffer) {
        int[] pixelsABGR = image.getPixelsRGBA(); // its ABGR

        for (int abgr : pixelsABGR) {
            int a = (abgr >> 24) & 0xFF;
            int b = (abgr >> 16) & 0xFF;
            int g = (abgr >> 8) & 0xFF;
            int r = abgr & 0xFF;

            buffer.put((byte) r);
            buffer.put((byte) g);
            buffer.put((byte) b);
            buffer.put((byte) a);
        }

        buffer.flip();
    }

    public static String toBase64String(NativeImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        WritableByteChannel channel = Channels.newChannel(baos);

        boolean success = ((NativeImageAccess) (Object) image).invokeWriteToChannel(channel);
        channel.close();

        if (!success) throw new IOException("Failed to write NativeImage to PNG bytes.");

        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public static NativeImage fromBase64String(String base64) throws IOException {
        byte[] bytes = Base64.getDecoder().decode(base64);
        return NativeImage.read(bytes);
    }
}
