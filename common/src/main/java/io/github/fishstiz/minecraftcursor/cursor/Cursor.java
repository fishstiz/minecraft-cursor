package io.github.fishstiz.minecraftcursor.cursor;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.compat.ExternalCursorTracker;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.util.NativeImageUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

import static io.github.fishstiz.minecraftcursor.util.SettingsUtil.*;

public class Cursor {
    protected static final int SIZE = 32;
    protected final Consumer<Cursor> onLoad;
    private final CursorType type;
    private ResourceLocation sprite;
    private String base64Image;
    private double scale;
    private int xhot;
    private int yhot;
    private boolean enabled;
    private boolean loaded;
    private int trueWidth;
    private int trueHeight;
    private long id = 0;

    public Cursor(CursorType type, Consumer<Cursor> onLoad) {
        this.type = type;
        this.onLoad = onLoad;
    }

    public void loadImage(ResourceLocation sprite, NativeImage image, CursorConfig.Settings settings) throws IOException {
        this.trueWidth = image.getWidth();
        this.trueHeight = image.getHeight();

        NativeImage croppedImage = image;
        try {
            if (image.getWidth() > SIZE || image.getHeight() > SIZE) {
                croppedImage = NativeImageUtil.cropImage(croppedImage, 0, 0, SIZE, SIZE);
            }

            this.sprite = sprite;
            this.base64Image = NativeImageUtil.toBase64String(croppedImage);
            this.enabled = settings.isEnabled();

            create(croppedImage, settings.getScale(), settings.getXHot(), settings.getYHot());
        } finally {
            croppedImage.close();
        }
    }

    protected void updateImage(double scale, int xhot, int yhot) {
        if (id == 0 || base64Image == null) {
            return;
        }

        try (NativeImage image = NativeImageUtil.fromBase64String(base64Image)) {
            create(image, sanitizeScale(scale), sanitizeHotspot(xhot), sanitizeHotspot(yhot));
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Error updating image of {}: {}", type, e);
        }
    }

    private void create(NativeImage image, double scale, int xhot, int yhot) {
        double correctedScale = isAutoScale(scale) ? Minecraft.getInstance().getWindow().getGuiScale() : scale;

        try (NativeImage scaledImage = scale == 1 ? image : NativeImageUtil.scaleImage(image, correctedScale)) {
            int scaledXHot = scale == 1 ? xhot : (int) Math.round(xhot * correctedScale);
            int scaledYHot = scale == 1 ? yhot : (int) Math.round(yhot * correctedScale);
            int scaledWidth = scaledImage.getWidth();
            int scaledHeight = scaledImage.getHeight();

            GLFWImage glfwImage = GLFWImage.create();
            glfwImage.width(scaledWidth);
            glfwImage.height(scaledHeight);

            ByteBuffer pixels = MemoryUtil.memAlloc(scaledWidth * scaledHeight * 4);
            NativeImageUtil.writePixelsRGBA(scaledImage, pixels);
            glfwImage.pixels(pixels);
            MemoryUtil.memFree(pixels);

            long previousId = this.id;
            ExternalCursorTracker.get().storeAddress(glfwImage.address());
            this.id = GLFW.glfwCreateCursor(glfwImage, scaledXHot, scaledYHot);

            if (this.onLoad != null) {
                this.onLoad.accept(this);
            }

            if (previousId != 0 && this.id != previousId) {
                GLFW.glfwDestroyCursor(previousId);
            }

            loaded = true;
            this.scale = scale;
            this.xhot = xhot;
            this.yhot = yhot;
        }
    }

    public void destroy() {
        if (this.id != 0) {
            GLFW.glfwDestroyCursor(this.id);
        }
    }

    public void reload() {
        this.updateImage(this.getScale(), this.getXHot(), this.getYHot());
    }

    public void applySettings(CursorConfig.Settings settings) {
        this.enable(settings.isEnabled());
        this.updateImage(settings.getScale(), settings.getXHot(), settings.getYHot());
    }

    public void enable(boolean enabled) {
        this.enabled = enabled;
        if (this.onLoad != null) this.onLoad.accept(this);
    }

    public ResourceLocation getSprite() {
        return sprite;
    }

    public long getId() {
        return enabled ? id : 0;
    }

    public CursorType getType() {
        return type;
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        updateImage(scale, this.xhot, this.yhot);
    }

    public int getXHot() {
        return this.xhot;
    }

    public void setXHot(double xhot) {
        setXHot((int) xhot);
    }

    public void setXHot(int xhot) {
        updateImage(this.scale, xhot, this.yhot);
    }

    public int getYHot() {
        return this.yhot;
    }

    public void setYHot(double yhot) {
        setYHot((int) yhot);
    }

    public void setYHot(int yhot) {
        updateImage(this.scale, this.xhot, yhot);
    }

    public void setHotspots(int xhot, int yhot) {
        updateImage(this.scale, xhot, yhot);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public int getTrueWidth() {
        return trueWidth;
    }

    public int getTrueHeight() {
        return trueHeight;
    }
}
