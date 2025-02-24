package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.CursorTypeRegistrar;
import io.github.fishstiz.minecraftcursor.config.AnimatedCursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import io.github.fishstiz.minecraftcursor.cursor.AnimationState;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;

public final class CursorManager implements CursorTypeRegistrar {
    private static final CursorManager INSTANCE = new CursorManager();
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final LinkedHashMap<String, Cursor> cursors = new LinkedHashMap<>();
    private final TreeMap<Integer, String> overrides = new TreeMap<>();
    private final AnimationState animationState = new AnimationState();
    private Cursor currentCursor = new Cursor(CursorType.of(""), null);

    private CursorManager() {
    }

    public static CursorManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(CursorType... cursorTypes) {
        for (CursorType cursorType : cursorTypes) {
            cursors.put(cursorType.getKey(), new Cursor(cursorType, this::handleCursorLoad));
        }
    }

    @Override
    public CursorType register(String key) {
        CursorType cursorType = CursorType.of(key);
        cursors.put(key, new Cursor(cursorType, this::handleCursorLoad));
        return cursorType;
    }

    public void loadCursorImage(
            CursorType type,
            Identifier sprite,
            BufferedImage image,
            CursorConfig.Settings settings,
            @Nullable AnimatedCursorConfig animation
    ) throws IOException {
        boolean animated = animation != null;
        Cursor cursor = getCursor(type);
        CursorConfig.Settings updatedSettings = getUpdatedSettings(settings);

        if (animated != (cursor instanceof AnimatedCursor)) {
            cursor.destroy();
            cursor = createAppropiateCursor(type, animated);
            cursors.put(type.getKey(), cursor);
        }

        if (cursor instanceof AnimatedCursor animatedCursor) {
            animatedCursor.loadImage(sprite, image, updatedSettings, animation);
        } else {
            cursor.loadImage(sprite, image, updatedSettings);
        }
    }

    private CursorConfig.Settings getUpdatedSettings(CursorConfig.Settings settings) {
        CursorConfig.GlobalSettings global = CONFIG.getGlobal();
        CursorConfig.Settings updatedSettings = new CursorConfig.Settings();

        updatedSettings.update(
                global.isScaleActive() ? global.getScale() : settings.getScale(),
                global.isXHotActive() ? global.getXHot() : settings.getXHot(),
                global.isYHotActive() ? global.getYHot() : settings.getYHot(),
                settings.isEnabled()
        );

        if (settings.isAnimated() != null) {
            updatedSettings.setAnimated(settings.isAnimated());
        }

        return updatedSettings;
    }

    private Cursor createAppropiateCursor(CursorType type, boolean animated) {
        return animated
                ? new AnimatedCursor(type, this::handleCursorLoad)
                : new Cursor(type, this::handleCursorLoad);
    }

    private void handleCursorLoad(Cursor cursor) {
        if (getCurrentCursor().getId() == cursor.getId()) {
            reloadCursor();
        }
    }

    void setCurrentCursor(CursorType type) {
        Cursor override = getOverride().orElse(null);
        Cursor cursor = override != null ? override : getCursor(type.getKey());

        if (cursor instanceof AnimatedCursor animatedCursor && cursor.getId() != 0) {
            handleCursorAnimation(animatedCursor);
            return;
        }

        if (type != CursorType.DEFAULT && cursor.getId() == 0 || !cursor.isEnabled()) {
            cursor = getCursor(CursorType.DEFAULT);
        }

        updateCursor(cursor);
    }

    private void handleCursorAnimation(AnimatedCursor cursor) {
        if (cursor == null) {
            updateCursor(getCursor(CursorType.DEFAULT));
            return;
        }

        if (!getCurrentCursor().getType().getKey().equals(cursor.getType().getKey())) {
            animationState.reset();
        } else {
            animationState.nextFrame(cursor);
        }

        Cursor currentFrameCursor = cursor.getFrame(animationState.getCurrentFrame()).cursor();
        updateCursor(currentFrameCursor.getId() != 0 ? currentFrameCursor : cursor);
    }

    private void updateCursor(Cursor cursor) {
        if (currentCursor != null && cursor.getId() == currentCursor.getId()) {
            return;
        }

        currentCursor = cursor;
        GLFW.glfwSetCursor(client.getWindow().getHandle(), currentCursor.getId());
    }

    public void reloadCursor() {
        GLFW.glfwSetCursor(client.getWindow().getHandle(), getCurrentCursor().getId());
    }

    public void overrideCurrentCursor(CursorType type, int index) {
        if (getCursor(type).isEnabled()) {
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
            Cursor cursor = getCursor(lastEntry.getValue());

            if (cursor.getId() == 0) {
                overrides.remove(lastEntry.getKey());
            } else {
                return Optional.of(cursor);
            }
        }

        return Optional.empty();
    }

    public @NotNull Cursor getCurrentCursor() {
        Cursor override = getOverride().orElse(null);
        Cursor cursor = override != null ? override : currentCursor;

        if (cursor instanceof AnimatedCursor animatedCursor) {
            return animatedCursor.getFrame(animationState.getCurrentFrame()).cursor();
        }

        return cursor;
    }

    public @NotNull Cursor getCursor(String key) {
        return cursors.computeIfAbsent(key, k -> new Cursor(CursorType.of(k), this::handleCursorLoad));
    }

    public @NotNull Cursor getCursor(CursorType type) {
        return cursors.computeIfAbsent(type.getKey(), k -> new Cursor(type, this::handleCursorLoad));
    }

    public List<CursorType> getCursorTypes() {
        return cursors.keySet().stream().map(CursorType::of).toList();
    }

    public List<Cursor> getLoadedCursors() {
        List<Cursor> activeCursors = new ArrayList<>();
        for (Cursor cursor : cursors.values()) {
            if (cursor.isLoaded()) {
                activeCursors.add(cursor);
            }
        }
        return activeCursors;
    }

    public boolean isAdaptive() {
        return cursors.values().stream().anyMatch(cursor ->
                cursor.isEnabled() && CursorType.DEFAULT != cursor.getType()
        );
    }

    public void setIsAdaptive(boolean isAdaptive) {
        cursors.values().forEach(cursor -> {
            if (cursor.getType() != CursorType.DEFAULT) {
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
