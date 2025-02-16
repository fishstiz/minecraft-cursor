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
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class AnimatedCursor extends Cursor {
    private AnimationMode mode = AnimationMode.LOOP;
    private HashMap<Integer, Cursor> cursors = new HashMap<>();
    private List<Frame> frames = new ArrayList<>();
    private boolean animated = true;

    public AnimatedCursor(CursorType type, Consumer<Cursor> onLoad) {
        super(type, onLoad);
    }

    public void loadImage(
            Identifier sprite,
            BufferedImage image,
            CursorConfig.Settings settings,
            AnimatedCursorConfig config
    ) throws IOException, UncheckedIOException {
        super.loadImage(sprite, image, settings);

        HashMap<Integer, Cursor> tempCursors = new HashMap<>();
        List<Frame> tempFrames = new ArrayList<>();
        int availableFrames = image.getHeight() / SIZE;

        for (int i = 1; i < availableFrames; i++) {
            Cursor cursor = createCursor(sprite, image, settings, i);
            tempCursors.put(i, cursor);

            if (config.getFrames().isEmpty()) {
                tempFrames.add(new Frame(cursor, config.getFrametime(), i));
            }
        }

        if (!config.getFrames().isEmpty()) {
            for (AnimatedCursorConfig.Frame frame : config.getFrames()) {
                int i = frame.getIndex();
                if (i < 0 || i >= availableFrames) {
                    MinecraftCursor.LOGGER.warn("Sprite does not exist on index {}, skipping frame.", i);
                    continue;
                }
                tempFrames.add(new Frame(i == 0 ? this : tempCursors.get(i), frame.getTime(config), i));
            }
        } else {
            tempFrames.addFirst(new Frame(this, config.getFrametime(), 0));
        }

        this.animated = settings.isAnimated() == null || settings.isAnimated();
        this.mode = config.mode;
        this.frames = tempFrames;

        List<Cursor> oldCursors = List.copyOf(this.cursors.values());
        this.cursors = tempCursors;
        oldCursors.forEach(Cursor::destroy);
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
            return frames.getFirst();
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

    public record Frame(Cursor cursor, int time, int spriteIndex) {
    }
}
