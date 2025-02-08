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
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final LinkedHashMap<CursorType, Cursor> CURSORS = new LinkedHashMap<>();
    private final TreeMap<Integer, CursorType> currentCursorOverrides = new TreeMap<>();
    private Cursor currentCursor;
    private long previousCursorId;

    CursorManager() {
        for (CursorType cursorType : CursorTypeRegistry.types()) {
            CURSORS.put(cursorType, new Cursor(cursorType));
        }
    }

    public void loadCursorImage(CursorType type, Identifier sprite, BufferedImage image, CursorConfig.Settings settings) throws IOException {
        Cursor cursor = CURSORS.computeIfAbsent(type, Cursor::new);
        cursor.loadImage(sprite, image, settings.getScale(), settings.getXHot(), settings.getYHot(), settings.isEnabled());

        if (currentCursor == null) {
            setCurrentCursor(cursor.getType());
            return;
        }
        if (currentCursor.getType() == type) {
            reloadCursor();
        }
    }

    public void setCurrentCursor(CursorType type) {
        Cursor cursor = CURSORS.get(currentCursorOverrides.isEmpty() ? type : currentCursorOverrides.lastEntry().getValue());

        if (cursor == null || (type != CursorType.DEFAULT && cursor.getId() == 0) || !cursor.isEnabled()) {
            cursor = CURSORS.get(CursorType.DEFAULT);
        }

        if (cursor == null || !cursor.isLoaded()) {
            return;
        }

        if (currentCursor != null && cursor.getId() == previousCursorId) {
            return;
        }

        currentCursor = cursor;
        previousCursorId = cursor.getId();
        GLFW.glfwSetCursor(CLIENT.getWindow().getHandle(), currentCursor.getId());
    }

    public void overrideCurrentCursor(CursorType type, int index) {
        if (getCursor(type).isEnabled()) {
            currentCursorOverrides.put(index, type);
        } else {
            currentCursorOverrides.remove(index);
        }
    }

    public void removeOverride(int index) {
        currentCursorOverrides.remove(index);
    }

    public void reloadCursor() {
        long id = currentCursorOverrides.isEmpty() ?
                currentCursor.getId() :
                getCursor(currentCursorOverrides.lastEntry().getValue()).getId();

        GLFW.glfwSetCursor(CLIENT.getWindow().getHandle(), id);
    }

    public Cursor getCurrentCursor() {
        return currentCursorOverrides.isEmpty() ? currentCursor :
                getCursor(currentCursorOverrides.lastEntry().getValue());
    }

    public Cursor getCursor(CursorType type) {
        return CURSORS.get(type);
    }

    public List<Cursor> getLoadedCursors() {
        List<Cursor> activeCursors = new ArrayList<>();
        for (Cursor cursor : CURSORS.values()) {
            if (cursor.isLoaded()) {
                activeCursors.add(cursor);
            }
        }
        return activeCursors;
    }

    public boolean isAdaptive() {
        return CURSORS.values().stream().anyMatch(cursor ->
                cursor.isEnabled() && CursorType.DEFAULT != cursor.getType()
        );
    }

    public void setIsAdaptive(boolean isAdaptive) {
        CURSORS.values().forEach(cursor -> {
            if (cursor.getType() != CursorType.DEFAULT) {
                cursor.enable(isAdaptive);
            }
        });
    }
}
