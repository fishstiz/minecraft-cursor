package io.github.fishstiz.minecraftcursor.cursor;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.AnimatedCursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.util.BufferedImageUtil;
import net.minecraft.util.Identifier;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
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
            Identifier sprite,
            BufferedImage image,
            CursorConfig.Settings settings,
            AnimatedCursorConfig animation
    ) throws IOException {
        super.loadImage(sprite, image, settings);

        int availableFrames = image.getHeight() / SIZE;

        HashMap<Integer, Cursor> newCursors = createCursors(sprite, image, settings, availableFrames);
        List<Frame> newFrames = createFrames(animation, newCursors, availableFrames);

        updateState(settings, animation, newCursors, newFrames);
    }

    private HashMap<Integer, Cursor> createCursors(
            Identifier sprite,
            BufferedImage image,
            CursorConfig.Settings settings,
            int availableFrames
    ) throws IOException {
        HashMap<Integer, Cursor> newCursors = new HashMap<>();
        for (int i = 1; i < availableFrames; i++) {
            newCursors.put(i, createCursor(sprite, image, settings, i));
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
                MinecraftCursor.LOGGER.warn("Sprite does not exist on index {} for cursor type '{}', skipping frame.", index, getType());
                continue;
            }
            newFrames.add(new Frame(index == 0 ? this : cursors.get(index), frame.getTime(animation), index));
        }
        return newFrames;
    }

    private Cursor createCursor(
            Identifier sprite,
            BufferedImage image,
            CursorConfig.Settings settings,
            int index
    ) throws IOException {
        Cursor cursor = new Cursor(this.getType(), this.onLoad);
        BufferedImage cropped = BufferedImageUtil.cropImage(image, new Rectangle(0, index * SIZE, SIZE, SIZE));
        cursor.loadImage(sprite, cropped, settings);
        cropped.flush();
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
        Frame frame = frames.get(index);
        if (!isAnimated() || frame.cursor() == null || !frame.cursor().isEnabled()) {
            return this.fallbackFrame != null ? this.fallbackFrame : new Frame(this, 1, 0);
        }
        return frame;
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
    public void enable(boolean enabled) {
        super.enable(enabled);
        applyToFrames(cursor -> cursor.enable(enabled));
    }

    public record Frame(Cursor cursor, int time, int spriteIndex) {
    }
}
