package io.github.fishstiz.minecraftcursor.cursor;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.fishstiz.minecraftcursor.CursorLoader;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.compat.ExternalCursorTracker;
import io.github.fishstiz.minecraftcursor.config.Config;
import io.github.fishstiz.minecraftcursor.util.NativeImageUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

import static io.github.fishstiz.minecraftcursor.util.SettingsUtil.*;

public class Cursor {
    private static final String IMG_TYPE = ".png";
    protected final Consumer<Cursor> onLoad;
    private final CursorType type;
    private final ResourceLocation location;
    private Component text;
    private String base64Image;
    private double scale;
    private int xhot;
    private int yhot;
    private boolean enabled;
    private boolean loaded;
    private int textureWidth;
    private int textureHeight;
    private long id = 0;

    public Cursor(CursorType type, Consumer<Cursor> onLoad) {
        this.type = type;
        this.onLoad = onLoad;
        this.location = CursorLoader.getDirectory().withSuffix(type.getKey() + IMG_TYPE);
    }

    public void loadImage(@NotNull NativeImage image, Config.Settings settings) throws IOException {
        this.textureWidth = image.getWidth();
        this.textureHeight = image.getHeight();

        if (!SUPPORTED_SIZES.contains(textureWidth) || textureHeight % textureWidth != 0) {
            throw new IOException("Invalid cursor size. Width must be one of " + SUPPORTED_SIZES + ", and height must be a multiple of width.");
        }

        boolean cropped = false;
        NativeImage croppedImage = image;

        try {
            int size = this.textureWidth;
            if (image.getHeight() > size) {
                croppedImage = NativeImageUtil.cropImage(image, 0, 0, size, size);
                cropped = true;
            }

            this.base64Image = NativeImageUtil.toBase64String(croppedImage);
            this.enabled = settings.isEnabled();

            create(croppedImage, settings.getScale(), settings.getXHot(), settings.getYHot());
        } finally {
            if (cropped) {
                croppedImage.close();
            }
        }
    }

    protected void updateImage(double scale, int xhot, int yhot) {
        if (!this.isLoaded()) {
            return;
        }

        try (NativeImage image = NativeImageUtil.fromBase64String(base64Image)) {
            create(image, scale, xhot, yhot);
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Error updating image of {}: {}", type, e);
        }
    }

    private void create(NativeImage image, double scale, int xhot, int yhot) {
        scale = sanitizeScale(scale);
        xhot = sanitizeHotspot(xhot, this);
        yhot = sanitizeHotspot(yhot, this);

        long glfwImageAddress = 0;
        long previousId = this.id;
        ByteBuffer pixels = null;

        double autoScaled = getAutoScale(scale);
        try (NativeImage scaledImage = scale == 1 ? image : NativeImageUtil.scaleImage(image, autoScaled)) {
            int scaledXHot = scale == 1 ? xhot : (int) Math.round(xhot * autoScaled);
            int scaledYHot = scale == 1 ? yhot : (int) Math.round(yhot * autoScaled);
            int scaledWidth = scaledImage.getWidth();
            int scaledHeight = scaledImage.getHeight();

            GLFWImage glfwImage = GLFWImage.create();
            pixels = MemoryUtil.memAlloc(scaledWidth * scaledHeight * 4);
            NativeImageUtil.writePixelsRGBA(scaledImage, pixels);
            glfwImage.set(scaledWidth, scaledHeight, pixels);

            glfwImageAddress = glfwImage.address();
            ExternalCursorTracker.get().claimAddress(glfwImageAddress);
            this.id = GLFW.glfwCreateCursor(glfwImage, scaledXHot, scaledYHot);

            if (this.id == MemoryUtil.NULL) {
                MinecraftCursor.LOGGER.error("[minecraft-cursor] Error creating cursor '{}'. ", this.type.getKey());
                return;
            }

            loaded = true;
            this.scale = scale;
            this.xhot = xhot;
            this.yhot = yhot;

            if (this.onLoad != null) {
                this.onLoad.accept(this);
            }
        } finally {
            if (glfwImageAddress != MemoryUtil.NULL) {
                ExternalCursorTracker.get().unclaimAddress(glfwImageAddress);
            }
            if (pixels != null) {
                MemoryUtil.memFree(pixels);
            }
            if (previousId != 0 && this.id != previousId) {
                GLFW.glfwDestroyCursor(previousId);
            }
        }
    }

    public void destroy() {
        if (this.id != 0) {
            GLFW.glfwDestroyCursor(this.id);
        }
    }

    public void reload() {
        if (this.isLoaded()) {
            this.updateImage(this.getScale(), this.getXHot(), this.getYHot());
        }
    }

    public void applySettings(Config.Settings settings) {
        this.enable(settings.isEnabled());
        this.updateImage(settings.getScale(), settings.getXHot(), settings.getYHot());
    }

    protected void enableWithoutLoading(boolean enabled) {
        this.enabled = enabled;
        if (this.onLoad != null) this.onLoad.accept(this);
    }

    public boolean enable(boolean enabled) {
        if (!this.isLoaded() && !this.enabled && enabled && !CursorLoader.loadCursorTexture(this)) {
            return false;
        }
        this.enabled = enabled;
        if (this.onLoad != null) {
            this.onLoad.accept(this);
        }
        return true;
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public long getId() {
        return enabled ? id : 0;
    }

    public @NotNull CursorType getType() {
        return type;
    }

    public @NotNull String getTypeKey() {
        return type.getKey();
    }

    public @NotNull Component getText() {
        if (this.text == null) {
            this.text = Component.translatable("minecraft-cursor.options.cursor-type." + type.getKey());
        }
        return this.text;
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

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }
}
