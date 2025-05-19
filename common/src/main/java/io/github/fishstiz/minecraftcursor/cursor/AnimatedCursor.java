package io.github.fishstiz.minecraftcursor.cursor;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.AnimatedCursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.util.NativeImageUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class AnimatedCursor extends Cursor {
    private AnimationMode mode = AnimationMode.LOOP;
    private HashMap<Integer, Cursor> cursors = new HashMap<>();
    private List<Frame> frames = new ArrayList<>();
    private boolean animated = true;
    private Frame fallbackFrame;

    public AnimatedCursor(CursorType type, Consumer<Cursor> onLoad) {
        super(type, onLoad);
    }

    public void loadImage(
            NativeImage image,
            CursorConfig.Settings settings,
            AnimatedCursorConfig animation
    ) throws IOException {
        super.loadImage(image, settings);

        int availableFrames = image.getHeight() / SIZE;

        HashMap<Integer, Cursor> newCursors = createCursors(image, settings, availableFrames);
        List<Frame> newFrames = createFrames(animation, newCursors, availableFrames);

        updateState(settings, animation, newCursors, newFrames);
    }

    private HashMap<Integer, Cursor> createCursors(
            NativeImage image,
            CursorConfig.Settings settings,
            int availableFrames
    ) throws IOException {
        HashMap<Integer, Cursor> newCursors = new HashMap<>();
        for (int i = 1; i < availableFrames; i++) {
            newCursors.put(i, createCursor(image, settings, i));
        }
        return newCursors;
    }

    private List<Frame> createFrames(AnimatedCursorConfig animation, HashMap<Integer, Cursor> cursors, int availableFrames) {
        List<Frame> newFrames = new ArrayList<>();

        if (animation.getFrames().isEmpty()) {
            newFrames.add(new Frame(this, animation.getFrametime(), 0));
            for (int i = 1; i < availableFrames; i++) {
                newFrames.add(new Frame(cursors.get(i), animation.getFrametime(), i));
            }
            return newFrames;
        }

        for (AnimatedCursorConfig.Frame frame : animation.getFrames()) {
            int index = frame.getIndex();
            if (index < 0 || index >= availableFrames) {
                MinecraftCursor.LOGGER.warn("[minecraft-cursor] Sprite does not exist on index {} for cursor type '{}', skipping frame.", index, getType());
                continue;
            }
            newFrames.add(new Frame(index == 0 ? this : cursors.get(index), frame.getTime(animation), index));
        }
        return newFrames;
    }

    private Cursor createCursor(
            NativeImage image,
            CursorConfig.Settings settings,
            int index
    ) throws IOException {
        Cursor cursor = new Cursor(this.getType(), this.onLoad);
        try (NativeImage cropped = NativeImageUtil.cropImage(image, 0, index * SIZE, SIZE, SIZE)) {
            cursor.loadImage(cropped, settings);
        }
        return cursor;
    }

    private void updateState(
            CursorConfig.Settings settings,
            AnimatedCursorConfig animation,
            HashMap<Integer, Cursor> newCursors,
            List<Frame> newFrames
    ) {
        this.fallbackFrame = new Frame(this, 1, 0);
        this.animated = settings.isAnimated() == null || settings.isAnimated();
        this.mode = animation.mode;

        boolean isReversed = this.mode == AnimationMode.LOOP_REVERSE || this.mode == AnimationMode.REVERSE;
        this.frames = isReversed ? newFrames.reversed() : newFrames;

        List<Cursor> oldCursors = List.copyOf(this.cursors.values());
        this.cursors = newCursors;
        oldCursors.forEach(Cursor::destroy);
    }

    @Override
    protected void updateImage(double scale, int xhot, int yhot) {
        super.updateImage(scale, xhot, yhot);
        applyToFrames(cursor -> cursor.updateImage(scale, xhot, yhot));
    }

    private void applyToFrames(Consumer<Cursor> action) {
        for (Cursor cursor : cursors.values()) {
            action.accept(cursor);
        }
    }

    public int getFrameCount() {
        return frames.size();
    }

    public Frame getFrame(int index) {
        try {
            Frame frame = frames.get(index);
            if (!isAnimated() || frame.cursor() == null || !frame.cursor().isEnabled()) {
                return getFallbackFrame();
            }
            return frame;
        } catch (IndexOutOfBoundsException e) {
            return getFallbackFrame();
        }
    }

    public boolean isAnimated() {
        return this.animated;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public AnimationMode getMode() {
        return this.mode;
    }

    @Override
    public boolean enable(boolean enabled) {
        if (super.enable(enabled)) {
            applyToFrames(cursor -> cursor.enableWithoutLoading(enabled));
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {
        super.destroy();
        applyToFrames(Cursor::destroy);
    }

    @Override
    public void reload() {
        super.reload();
        applyToFrames(Cursor::reload);
    }

    public Frame getFallbackFrame() {
        if (this.fallbackFrame == null) {
            this.fallbackFrame = new Frame(this, 1, 0);
        }
        return this.fallbackFrame;
    }

    public record Frame(Cursor cursor, int time, int spriteIndex) {
    }
}
