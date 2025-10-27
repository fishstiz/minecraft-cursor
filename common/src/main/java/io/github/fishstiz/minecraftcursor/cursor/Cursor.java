package io.github.fishstiz.minecraftcursor.cursor;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.fishstiz.minecraftcursor.CursorResourceLoader;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.compat.ExternalCursorTracker;
import io.github.fishstiz.minecraftcursor.config.Config;
import io.github.fishstiz.minecraftcursor.util.NativeImageUtil;
import io.github.fishstiz.minecraftcursor.util.SettingsUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

import static io.github.fishstiz.minecraftcursor.util.SettingsUtil.*;

public class Cursor {
    private static final String IMG_TYPE = ".png";
    private final @Nullable Consumer<Cursor> onLoad;
    private final CursorType type;
    private final ResourceLocation location;
    private final boolean editable; // band-aid field. Cursor should've been an interface, see v4 CursorTexture.
    private Component text;
    private byte[] pixels;
    private double scale;
    private int xhot;
    private int yhot;
    private boolean enabled;
    private boolean loaded;
    private int textureWidth;
    private int textureHeight;
    private long id = MemoryUtil.NULL;

    Cursor(CursorType type, @Nullable Consumer<Cursor> onLoad, boolean editable) {
        this.type = type;
        this.onLoad = onLoad;
        this.location = CursorResourceLoader.getDirectory().withSuffix(type.getKey() + IMG_TYPE);
        this.editable = editable;
    }

    Cursor(CursorType type, @Nullable Consumer<Cursor> onLoad) {
        this(type, onLoad, true);
    }

    protected Cursor(Cursor cursor, boolean editable) {
        this(cursor.type, cursor.onLoad, editable);
    }

    void loadImage(@NotNull NativeImage image, Config.Settings settings) throws IOException {
        try {
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            SettingsUtil.assertImageSize(imageWidth, imageHeight);

            NativeImage croppedImage = null;
            try {
                if (imageHeight > imageWidth) {
                    // noinspection SuspiciousNameCombination
                    croppedImage = NativeImageUtil.cropImage(image, 0, 0, imageWidth, imageWidth);
                }

                NativeImage validImage = croppedImage != null ? croppedImage : image;

                create(validImage, settings.getScale(), settings.getXHot(), settings.getYHot());

                this.enabled = settings.isEnabled();
                this.textureWidth = imageWidth;
                this.textureHeight = imageHeight;

                if (this.editable) {
                    this.pixels = NativeImageUtil.getBytes(validImage);
                }
            } finally {
                if (croppedImage != null) {
                    croppedImage.close();
                }
            }
        } catch (Exception e) {
            this.destroy();
            throw e;
        }
    }

    protected void updateImage(double scale, int xhot, int yhot) {
        if (!this.editable || !this.isLoaded() || pixels == null) {
            return;
        }

        try (NativeImage image = NativeImage.read(pixels)) {
            create(image, scale, xhot, yhot);
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Error updating image of {}: {}", type, e);
        }
    }

    private void create(NativeImage image, double scale, int xhot, int yhot) {
        scale = sanitizeScale(scale);
        xhot = sanitizeHotspot(xhot, image.getWidth());
        yhot = sanitizeHotspot(yhot, image.getWidth());
        double autoScaled = getAutoScale(scale);

        long glfwImageAddress = MemoryUtil.NULL;
        long previousId = this.id;
        ByteBuffer pixels = null;
        NativeImage scaledImage = null;

        try {
            NativeImage validImage = image;
            if (scale != 1) {
                scaledImage = NativeImageUtil.scaleImage(image, autoScaled);
                validImage = scaledImage;
            }

            int scaledXHot = scale == 1 ? xhot : (int) Math.round(xhot * autoScaled);
            int scaledYHot = scale == 1 ? yhot : (int) Math.round(yhot * autoScaled);
            int scaledWidth = validImage.getWidth();
            int scaledHeight = validImage.getHeight();

            GLFWImage glfwImage = GLFWImage.create();
            pixels = MemoryUtil.memAlloc(scaledWidth * scaledHeight * 4);
            NativeImageUtil.writePixelsRGBA(validImage, pixels);
            glfwImage.set(scaledWidth, scaledHeight, pixels);

            glfwImageAddress = glfwImage.address();
            ExternalCursorTracker.trackInternalCursor(glfwImageAddress);
            this.id = GLFW.glfwCreateCursor(glfwImage, scaledXHot, scaledYHot);

            if (this.id == MemoryUtil.NULL) {
                MinecraftCursor.LOGGER.error("[minecraft-cursor] Error creating cursor '{}'. ", this.type.getKey());
                return;
            }

            loaded = true;
            this.scale = scale;
            this.xhot = xhot;
            this.yhot = yhot;

            this.notifyOnLoad();
        } finally {
            if (scaledImage != null) {
                scaledImage.close();
            }
            if (pixels != null) {
                MemoryUtil.memFree(pixels);
            }
            if (previousId != MemoryUtil.NULL && this.id != previousId) {
                GLFW.glfwDestroyCursor(previousId);
            }
            if (glfwImageAddress != MemoryUtil.NULL) {
                ExternalCursorTracker.consumeInternalCursor(glfwImageAddress);
            }
        }
    }

    public void destroy() {
        if (this.id != MemoryUtil.NULL) {
            GLFW.glfwDestroyCursor(this.id);
            this.id = MemoryUtil.NULL;
        }
        if (this.pixels != null) {
            this.pixels = null;
        }
    }

    protected void notifyOnLoad() {
        if (this.onLoad != null) {
            this.onLoad.accept(this);
        }
    }

    public void reload() {
        if (this.isLoaded()) {
            this.updateImage(this.getScale(), this.getXHot(), this.getYHot());
        }
    }

    public void apply(Config.Settings settings) {
        this.enable(settings.isEnabled());
        this.updateImage(settings.getScale(), settings.getXHot(), settings.getYHot());
    }

    public void enable(boolean enabled) {
        boolean previous = this.enabled;
        this.enabled = enabled;

        if (previous != this.enabled && this.isLoaded()) {
            this.notifyOnLoad();
        }
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public long getId() {
        return enabled ? id : MemoryUtil.NULL;
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

    public boolean isEnabled() {
        return enabled && isLoaded();
    }

    public boolean isLoaded() {
        return this.loaded && this.id != MemoryUtil.NULL;
    }

    public int getTextureIndex() {
        return 0;
    }

    public int getTextureWidth() throws IllegalStateException {
        assertLoaded();
        return textureWidth;
    }

    public int getTextureHeight() throws IllegalStateException {
        assertLoaded();
        return textureHeight;
    }

    private void assertLoaded() {
        if (!this.isLoaded()) {
            throw new IllegalStateException("Cursor has not been loaded");
        }
    }

    static Cursor createDummy() {
        return new Cursor(CursorType.of(""), null);
    }
}
