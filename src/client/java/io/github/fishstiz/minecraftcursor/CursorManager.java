package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

public class CursorManager {
    private final LinkedHashMap<String, Cursor> cursors = new LinkedHashMap<>();
    private final TreeMap<Integer, String> overrides = new TreeMap<>();
    private final MinecraftClient client;
    private Cursor currentCursor;

    CursorManager(MinecraftClient client) {
        this.client = client;

        for (CursorType cursorType : CursorTypeRegistry.types()) {
            cursors.put(cursorType.getKey(), new Cursor(cursorType, this::handleCursorLoad));
        }
    }

    public void loadCursorImage(CursorType type, Identifier sprite, BufferedImage image, CursorConfig.Settings settings) throws IOException {
        Cursor cursor = getCursor(type);
        cursor.loadImage(sprite, image, settings.getScale(), settings.getXHot(), settings.getYHot(), settings.isEnabled());
    }

    private void handleCursorLoad(CursorType cursorType) {
        if (getCurrentCursor() == null) {
            setCurrentCursor(cursorType);
        } else if (getCurrentCursor().getType() == cursorType) {
            reloadCursor();
        }
    }

    void setCurrentCursor(CursorType type) {
        Cursor cursor = getCursor(overrides.isEmpty() ? type.getKey() : overrides.lastEntry().getValue());

        if (cursor == null || (type != CursorType.DEFAULT && cursor.getId() == 0) || !cursor.isEnabled()) {
            cursor = getCursor(CursorType.DEFAULT);
        }

        if (cursor == null || (currentCursor != null && cursor.getId() == currentCursor.getId())) {
            return;
        }

        currentCursor = cursor;
        GLFW.glfwSetCursor(client.getWindow().getHandle(), currentCursor.getId());
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

    public void reloadCursor() {
        GLFW.glfwSetCursor(client.getWindow().getHandle(), getCurrentCursor().getId());
    }

    public Cursor getCurrentCursor() {
        return overrides.isEmpty()
                ? currentCursor
                : getCursor(overrides.lastEntry().getValue());
    }

    public Cursor getCursor(String key) {
        return cursors.computeIfAbsent(key, k -> new Cursor(CursorType.of(k), this::handleCursorLoad));
    }

    public Cursor getCursor(CursorType type) {
        return cursors.computeIfAbsent(type.getKey(), k -> new Cursor(type, this::handleCursorLoad));
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
}
