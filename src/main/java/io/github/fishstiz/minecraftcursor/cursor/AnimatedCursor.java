package io.github.fishstiz.minecraftcursor.cursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.AnimatedCursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.util.BufferedImageUtil;
import net.minecraft.util.Identifier;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AnimatedCursor extends Cursor {
    private List<Frame> frames = new ArrayList<>();
    private boolean animated = true;

    public AnimatedCursor(CursorType type, Consumer<CursorType> onLoad) {
        super(type, onLoad);
    }

    public void loadImage(
            Identifier sprite,
            BufferedImage image,
            CursorConfig.Settings settings,
            AnimatedCursorConfig config
    ) throws IOException {
        super.loadImage(sprite, image, settings);

        int frameCount = image.getHeight() / SIZE;
        List<Frame> temp = new ArrayList<>();
        temp.add(new Frame(this, config.getTime(0)));

        for (int i = 1; i < frameCount; i++) {
            Cursor cursor = new Cursor(this.getType(), this.onLoad);

            BufferedImage croppedFrame = BufferedImageUtil.cropImage(
                    image,
                    new Rectangle(0, i * SIZE, SIZE, SIZE)
            );

            cursor.loadImage(sprite, croppedFrame, settings);
            croppedFrame.flush();

            temp.add(new Frame(cursor, config.getTime(i)));
        }

        this.frames = temp;
    }

    public boolean isAnimated() {
        return this.animated;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public int getFrameCount() {
        return frames.size();
    }

    public Frame getFrame(int index) {
        Frame frame = frames.get(index);
        if (!isAnimated() || frame.cursor() == null || frame.cursor().isEnabled()) {
            return frames.getFirst();
        }
        return frame;
    }

    @Override
    public void setScale(double scale) {
        super.setScale(scale);
        applyToFrames(cursor -> cursor.setScale(scale));
    }

    @Override
    public void setXhot(int xhot) {
        super.setXhot(xhot);
        applyToFrames(cursor -> cursor.setXhot(xhot));
    }

    @Override
    public void setYhot(int yhot) {
        super.setYhot(yhot);
        applyToFrames(cursor -> cursor.setYhot(yhot));
    }

    @Override
    public void enable(boolean enabled) {
        super.enable(enabled);
        applyToFrames(cursor -> cursor.enable(enabled));
    }

    @Override
    public void enable() {
        super.enable();
        applyToFrames(Cursor::enable);
    }

    @Override
    public void disable() {
        super.disable();
        applyToFrames(Cursor::disable);
    }

    public void applyToFrames(Consumer<Cursor> action) {
        if (frames.size() == 1) return;

        for (int i = 1; i <= frames.size(); i++) {
            action.accept(frames.get(i).cursor());
        }
    }

    public record Frame(Cursor cursor, int time) {
    }
}
