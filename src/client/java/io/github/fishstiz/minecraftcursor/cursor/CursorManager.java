package io.github.fishstiz.minecraftcursor.cursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfigService;
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
    private final CursorConfigService config;
    private final MinecraftClient client;
    private final LinkedHashMap<CursorType, Cursor> cursors = new LinkedHashMap<>();
    private final TreeMap<Integer, CursorType> currentCursorOverrides = new TreeMap<>();
    private Cursor currentCursor;
    private long previousCursorId;

    public CursorManager(CursorConfigService config, MinecraftClient client) {
        this.config = config;
        this.client = client;

        for (CursorType type : CursorTypeRegistry.types()) {
            cursors.put(type, new Cursor((type)));
        }
    }

    public void loadCursorImage(CursorType type, Identifier sprite, BufferedImage image, CursorConfig.Settings settings) throws IOException {
        Cursor cursor = cursors.computeIfAbsent(type, Cursor::new);
        cursor.loadImage(sprite, image, settings.getScale(), settings.getXHot(), settings.getYHot(), settings.getEnabled());

        if (currentCursor == null) {
            setCurrentCursor(cursor.getType());
            return;
        }
        if (currentCursor.getType() == type) {
            reloadCursor();
        }
    }

    public void setCurrentCursor(CursorType type) {
        Cursor cursor = cursors.get(currentCursorOverrides.isEmpty() ? type : currentCursorOverrides.lastEntry().getValue());

        if (cursor == null || (type != CursorType.DEFAULT && cursor.getId() == 0) || !cursor.getEnabled()) {
            cursor = cursors.get(CursorType.DEFAULT);
        }

        if (currentCursor != null && cursor.getId() == previousCursorId) {
            return;
        }

        currentCursor = cursor;
        previousCursorId = cursor.getId();
        GLFW.glfwSetCursor(client.getWindow().getHandle(), currentCursor.getId());
    }

    public void overrideCurrentCursor(CursorType type, int index) {
        if (getCursor(type).getEnabled()) {
            currentCursorOverrides.put(index, type);
        } else {
            currentCursorOverrides.remove(index);
        }
    }

    public void removeOverride(int index) {
        currentCursorOverrides.remove(index);
    }

    public void clearOverrides() {
        currentCursorOverrides.clear();
    }

    public void reloadCursor() {
        long id = currentCursorOverrides.isEmpty() ?
                currentCursor.getId() :
                getCursor(currentCursorOverrides.lastEntry().getValue()).getId();

        GLFW.glfwSetCursor(client.getWindow().getHandle(), id);
    }

    public Cursor getCurrentCursor() {
        return currentCursorOverrides.isEmpty() ? currentCursor :
                getCursor(currentCursorOverrides.lastEntry().getValue());
    }

    public Cursor getCursor(CursorType type) {
        return cursors.get(type);
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
                cursor.getEnabled() && CursorType.DEFAULT != cursor.getType()
        );
    }

    public void setIsAdaptive(boolean isAdaptive) {
        cursors.values().forEach(cursor -> {
            if (cursor.getType() != CursorType.DEFAULT) {
                cursor.enable(isAdaptive);
            }
        });
    }

    public void saveAll() {
        config.saveSettings(true, cursors.values().toArray(new Cursor[0]));
    }

    public void saveCursor(CursorType type) {
        config.saveSettings(getCursor(type));
    }
}
