package io.github.fishstiz.minecraftcursor.cursor;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.AnimationData;
import io.github.fishstiz.minecraftcursor.config.Config;
import io.github.fishstiz.minecraftcursor.util.NativeImageUtil;
import io.github.fishstiz.minecraftcursor.util.SettingsUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class AnimatedCursor extends Cursor {
    private AnimationMode mode = AnimationMode.LOOP;
    private FrameCursor[] cursors = new FrameCursor[0];
    private FrameData[] frames = new FrameData[0];
    private boolean animated = true;
    private FrameData fallbackFrame;
    private AnimationData animation;
    private byte[] pixels;

    AnimatedCursor(CursorType type, Consumer<Cursor> onLoad) {
        super(type, onLoad, false);
    }

    void loadImage(NativeImage image, Config.Settings settings, AnimationData animation) throws IOException {
        super.loadImage(image, settings);

        Map<Integer, FrameCursor> newCursors = null;
        try {
            int availableFrames = image.getHeight() / this.getTextureWidth();

            newCursors = createCursors(image, settings, availableFrames);

            this.setAnimated(settings.isAnimated());
            this.fallbackFrame = new FrameData(this, 1);
            this.mode = animation.mode;
            this.frames = createFrames(animation, newCursors, availableFrames).toArray(FrameData[]::new);

            FrameCursor[] oldCursors = this.cursors;
            this.cursors = newCursors.values().toArray(FrameCursor[]::new);
            SettingsUtil.forEach(oldCursors, Cursor::destroy);

            this.animation = animation;
            this.pixels = NativeImageUtil.getBytes(image);
        } catch (Exception e) {
            if (newCursors != null) {
                newCursors.values().forEach(Cursor::destroy);
            }
            this.destroy();
            throw e;
        }
    }

    private Map<Integer, FrameCursor> createCursors(NativeImage image, Config.Settings settings, int availableFrames) throws IOException {
        Map<Integer, FrameCursor> newCursors = new Int2ObjectOpenHashMap<>(availableFrames - 1);
        try {
            for (int i = 1; i < availableFrames; i++) {
                newCursors.put(i, createCursor(image, settings, i));
            }
        } catch (IOException e) {
            newCursors.values().forEach(Cursor::destroy);
            throw e;
        }
        return newCursors;
    }

    private List<FrameData> createFrames(AnimationData animation, Map<Integer, FrameCursor> cursors, int availableFrames) {
        List<FrameData> newFrames = new ArrayList<>(animation.getFrames().size());

        if (animation.getFrames().isEmpty()) {
            newFrames.add(new FrameData(this, animation.getFrametime()));
            for (int i = 1; i < availableFrames; i++) {
                newFrames.add(new FrameData(cursors.get(i), animation.getFrametime()));
            }
            if (animation.mode.isReversed()) {
                Collections.reverse(newFrames);
            }
            return newFrames;
        }

        for (AnimationData.Frame frame : animation.getFrames()) {
            int index = frame.getIndex();
            if (index < 0 || index >= availableFrames) {
                MinecraftCursor.LOGGER.warn("[minecraft-cursor] Sprite does not exist on index {} for cursor type '{}', skipping frame.", index, getType());
                continue;
            }
            newFrames.add(new FrameData(index == 0 ? this : cursors.get(index), frame.getTime(animation)));
        }
        if (animation.mode.isReversed()) {
            Collections.reverse(newFrames);
        }
        return newFrames;
    }

    private FrameCursor createCursor(NativeImage image, Config.Settings settings, int index) throws IOException {
        FrameCursor cursor = new FrameCursor(index);
        int size = this.getTextureWidth();
        try (NativeImage cropped = NativeImageUtil.cropImage(image, 0, index * size, size, size)) {
            cursor.loadImage(cropped, settings);
        }
        return cursor;
    }

    @Override
    protected void updateImage(double scale, int xhot, int yhot) {
        if (!this.isLoaded() || pixels == null) {
            return;
        }

        try (NativeImage image = NativeImageUtil.readLarge(this.pixels)) {
            Config.Settings settings = new Config.Settings(scale, xhot, yhot, this.isEnabled(), this.isAnimated());
            int availableFrames = image.getHeight() / this.getTextureWidth();

            Map<Integer, FrameCursor> newCursors = createCursors(image, settings, availableFrames);
            FrameData[] newFrames = createFrames(this.animation, newCursors, availableFrames).toArray(FrameData[]::new);

            super.loadImage(image, settings);

            this.frames = newFrames;
            FrameCursor[] oldCursors = this.cursors;
            this.cursors = newCursors.values().toArray(FrameCursor[]::new);

            SettingsUtil.forEach(this.cursors, Cursor::notifyOnLoad);
            SettingsUtil.forEach(oldCursors, Cursor::destroy);
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("[minecraft-cursor] Failed to update animated cursor image. ", e);
        }
    }

    private void applyToFrames(Consumer<Cursor> action) {
        SettingsUtil.forEach(this.cursors, action);
    }

    public int getFrameCount() {
        return Math.max(frames.length, 1);
    }

    public FrameData getFrame(int index) {
        if (!isAnimated() || index < 0 || index >= frames.length) {
            return getFallbackFrame();
        }

        FrameData frame = frames[index];
        if (frame.cursor() == null || !frame.cursor().isEnabled()) {
            return getFallbackFrame();
        }
        return frame;
    }

    public FrameData nextFrame(AnimationState state) {
        return this.getFrame(state.next(this));
    }

    public boolean isAnimated() {
        return this.animated;
    }

    public void setAnimated(Boolean animated) {
        this.animated = animated == null || animated;
    }

    public AnimationMode getMode() {
        return this.mode;
    }

    @Override
    public void apply(Config.Settings settings) {
        this.setAnimated(settings.isAnimated());
        super.apply(settings);
    }

    @Override
    public void enable(boolean enabled) {
        super.enable(enabled);
        applyToFrames(cursor -> cursor.enable(enabled));
    }

    @Override
    public void destroy() {
        super.destroy();
        applyToFrames(Cursor::destroy);
        this.pixels = null;
    }

    @Override
    public void reload() {
        super.reload();
        applyToFrames(Cursor::reload);
    }

    public FrameData getFallbackFrame() {
        if (this.fallbackFrame == null) {
            this.fallbackFrame = new FrameData(this, 1);
        }
        return this.fallbackFrame;
    }

    public record FrameData(Cursor cursor, int time) {
    }

    private class FrameCursor extends Cursor {
        private final int textureIndex;

        private FrameCursor(int textureIndex) {
            super(AnimatedCursor.this, false);
            this.textureIndex = textureIndex;
        }

        @Override
        public boolean isLazy() {
            return false;
        }

        @Override
        public int getTextureIndex() {
            return this.textureIndex;
        }

        @Override
        public int getTextureWidth() throws IllegalStateException {
            return AnimatedCursor.this.getTextureWidth();
        }

        @Override
        public int getTextureHeight() throws IllegalStateException {
            return AnimatedCursor.this.getTextureHeight();
        }
    }
}
