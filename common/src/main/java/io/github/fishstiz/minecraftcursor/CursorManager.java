package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.CursorTypeRegistrar;
import io.github.fishstiz.minecraftcursor.config.AnimatedCursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import io.github.fishstiz.minecraftcursor.cursor.AnimationState;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;

public final class CursorManager implements CursorTypeRegistrar {
    public static final CursorManager INSTANCE = new CursorManager();
    private final LinkedHashMap<String, Cursor> cursors = new LinkedHashMap<>();
    private final TreeMap<Integer, String> overrides = new TreeMap<>();
    private final AnimationState animationState = new AnimationState();
    private @NotNull Cursor currentCursor = new Cursor(CursorType.of(""), null);

    private CursorManager() {
    }

    @Override
    public void register(CursorType... cursorTypes) {
        for (CursorType cursorType : cursorTypes) {
            register(cursorType);
        }
    }

    @Override
    public CursorType register(String key) {
        return register(CursorType.of(key));
    }

    private CursorType register(CursorType cursorType) {
        String key = cursorType.getKey();

        if (key.isEmpty()) {
            throw new NullPointerException("Cursor type key cannot be empty.");
        }

        if (cursors.containsKey(key)) {
            MinecraftCursor.LOGGER.error("[minecraft-cursor] Cursor type '{}' is already registered.", key);
            return cursorType;
        }

        cursors.put(key, new Cursor(cursorType, this::onLoad));

        return cursorType;
    }

    public void loadCursor(
            Cursor cursor,
            ResourceLocation sprite,
            BufferedImage image,
            @Nullable AnimatedCursorConfig animation
    ) throws IOException {
        boolean animated = animation != null;

        if (animated != (cursor instanceof AnimatedCursor)) {
            cursor.destroy();
            cursor = animated
                    ? new AnimatedCursor(cursor.getType(), this::onLoad)
                    : new Cursor(cursor.getType(), this::onLoad);

            cursors.put(cursor.getType().getKey(), cursor);
        }

        CursorConfig.Settings settings = getCursorSettings(cursor);
        if (cursor instanceof AnimatedCursor animatedCursor) {
            animatedCursor.loadImage(sprite, image, settings, animation);
        } else {
            cursor.loadImage(sprite, image, settings);
        }
    }

    private void onLoad(Cursor cursor) {
        Cursor appliedCursor = getAppliedCursor();
        if (appliedCursor.isLoaded() && appliedCursor.getId() == cursor.getId()) {
            reloadCursor();
        }
    }

    private CursorConfig.Settings getCursorSettings(Cursor cursor) {
        CursorConfig.Settings settings = CONFIG.getOrCreateCursorSettings(cursor.getType());
        CursorConfig.GlobalSettings global = CONFIG.getGlobal();

        settings.update(
                global.isScaleActive() ? global.getScale() : settings.getScale(),
                global.isXHotActive() ? global.getXHot() : settings.getXHot(),
                global.isYHotActive() ? global.getYHot() : settings.getYHot(),
                settings.isEnabled()
        );

        if (settings.isAnimated() != null) {
            settings.setAnimated(settings.isAnimated());
        }

        return settings;
    }

    void setCurrentCursor(@NotNull CursorType type) {
        Cursor override = getOverride().orElse(null);
        Cursor cursor = override != null ? override : this.cursors.get(type.getKey());

        if (cursor instanceof AnimatedCursor animatedCursor && cursor.getId() != 0) {
            handleCursorAnimation(animatedCursor);
            return;
        }

        if (cursor == null || !type.isDefault() && cursor.getId() == 0 || !cursor.isEnabled()) {
            cursor = getCursor(CursorType.DEFAULT);
        }

        updateCursor(cursor);
    }

    private void handleCursorAnimation(AnimatedCursor cursor) {
        if (!getAppliedCursor().getType().isKey(cursor.getType())) {
            animationState.reset();
        } else {
            animationState.nextFrame(cursor);
        }

        Cursor currentFrameCursor = cursor.getFrame(animationState.getCurrentFrame()).cursor();
        updateCursor(currentFrameCursor.getId() != 0 ? currentFrameCursor : cursor);
    }

    private void updateCursor(Cursor cursor) {
        if (cursor == null || cursor.getId() == currentCursor.getId()) {
            return;
        }

        currentCursor = cursor;
        GLFW.glfwSetCursor(CursorTypeUtil.WINDOW, currentCursor.getId());
    }

    public void reloadCursor() {
        GLFW.glfwSetCursor(CursorTypeUtil.WINDOW, getAppliedCursor().getId());
    }

    public void overrideCurrentCursor(CursorType type, int index) {
        Cursor cursor = getCursor(type);
        if (cursor != null && cursor.isEnabled()) {
            overrides.put(index, type.getKey());
        } else {
            overrides.remove(index);
        }
    }

    public void removeOverride(int index) {
        overrides.remove(index);
    }

    public Optional<Cursor> getOverride() {
        while (!overrides.isEmpty()) {
            Map.Entry<Integer, String> lastEntry = overrides.lastEntry();
            Cursor cursor = this.cursors.get(lastEntry.getValue());

            if (cursor == null || cursor.getId() == 0) {
                overrides.remove(lastEntry.getKey());
            } else {
                return Optional.of(cursor);
            }
        }

        return Optional.empty();
    }

    public @NotNull Cursor getAppliedCursor() {
        Cursor override = getOverride().orElse(null);
        Cursor cursor = override != null ? override : currentCursor;

        if (cursor instanceof AnimatedCursor animatedCursor) {
            return animatedCursor.getFrame(animationState.getCurrentFrame()).cursor();
        }

        return cursor;
    }

    public @Nullable Cursor getCursor(CursorType type) {
        return cursors.get(type.getKey());
    }

    public long getId(CursorType type) {
        Cursor cursor = cursors.get(type.getKey());
        return cursor != null ? cursor.getId() : 0;
    }

    public long getCurrentId() {
        return getAppliedCursor().getId();
    }

    public Collection<Cursor> getCursors() {
        return cursors.values();
    }

    public List<Cursor> getLoadedCursors() {
        List<Cursor> loadedCursors = new ArrayList<>();
        for (Cursor cursor : cursors.values()) {
            if (cursor.isLoaded()) {
                loadedCursors.add(cursor);
            }
        }
        return loadedCursors;
    }

    public boolean isAdaptive() {
        for (Cursor cursor : cursors.values()) {
            if (cursor.isEnabled() && !cursor.getType().isDefault()) {
                return true;
            }
        }
        return false;
    }

    public void setIsAdaptive(boolean isAdaptive) {
        cursors.values().forEach(cursor -> {
            if (!cursor.getType().isDefault()) {
                cursor.enable(isAdaptive);
            }
        });
    }

    public boolean hasAnimations() {
        for (Cursor cursor : cursors.values()) {
            if (cursor instanceof AnimatedCursor) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnimated() {
        for (Cursor cursor : cursors.values()) {
            if (cursor instanceof AnimatedCursor animatedCursor && animatedCursor.isAnimated()) {
                return true;
            }
        }
        return false;
    }

    public void setIsAnimated(boolean isAnimated) {
        cursors.values().forEach(cursor -> {
            if (cursor instanceof AnimatedCursor animatedCursor) {
                animatedCursor.setAnimated(isAnimated);
            }
        });
    }
}
