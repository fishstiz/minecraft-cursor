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

    public AnimatedCursor(CursorType type, Consumer<Cursor> onLoad) {
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
            BufferedImage cropped = BufferedImageUtil.cropImage(image, new Rectangle(0, i * SIZE, SIZE, SIZE));
            cursor.loadImage(sprite, cropped, settings);
            cropped.flush();
            temp.add(new Frame(cursor, config.getTime(i)));
        }

        this.frames = temp;
    }

    @Override
    protected void updateImage(double scale, int xhot, int yhot) {
        super.updateImage(scale, xhot, yhot);
        applyToFrames(cursor -> cursor.updateImage(scale, xhot, yhot));
    }

    private void applyToFrames(Consumer<Cursor> action) {
        if (frames.size() == 1) return;

        for (int i = 1; i < frames.size(); i++) {
            action.accept(frames.get(i).cursor());
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

    public record Frame(Cursor cursor, int time) {
    }
}
