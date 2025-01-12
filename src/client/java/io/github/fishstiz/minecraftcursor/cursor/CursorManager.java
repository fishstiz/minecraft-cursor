package io.github.fishstiz.minecraftcursor.cursor;

import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfigService;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.TreeMap;

public class CursorManager {
    private final CursorConfigService config;
    private final MinecraftClient client;
    private final EnumMap<CursorType, Cursor> cursors = new EnumMap<>(CursorType.class);
    private final TreeMap<Integer, CursorType> currentCursorOverrides = new TreeMap<>();
    private Cursor currentCursor;
    private long previousCursorId;

    public CursorManager(CursorConfigService config, MinecraftClient client) {
        this.config = config;
        this.client = client;

        for (CursorType type : CursorType.values()) {
            cursors.put(type, new Cursor((type)));
        }
    }

    public void loadCursorImage(CursorType type, BufferedImage image) throws IOException {
        Cursor cursor = cursors.get(type);
        CursorConfig.Settings settings = config.getSettings(type);
        cursor.loadImage(image, settings.getScale(), settings.getXHot(), settings.getYHot(), settings.getEnabled());

        if (currentCursor == null) {
            return;
        }

        if (currentCursor.getType() == type) {
            reloadCursor();
        }
    }

    public void setCurrentCursor(CursorType type) {
        Cursor cursor = cursors.get(currentCursorOverrides.isEmpty() ? type : currentCursorOverrides.lastEntry().getValue());

        if (cursor == null || (type != CursorType.DEFAULT && cursor.getId() == 0)) {
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
        currentCursorOverrides.put(index, type);
    }

    public void removeOverride(int index) {
        currentCursorOverrides.remove(index);
    }

    public void reloadCursor() {
        GLFW.glfwSetCursor(client.getWindow().getHandle(), currentCursor.getId());
    }

    public Cursor getCurrentCursor() {
        return currentCursor;
    }

    public Cursor getCursor(CursorType type) {
        return cursors.get(type);
    }
}
