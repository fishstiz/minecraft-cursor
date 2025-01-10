package io.github.fishstiz.minecraftcursor.cursor;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;

public class CursorManager {
    private final CursorConfig config;
    private final MinecraftClient client;
    private final EnumMap<CursorType, Cursor> cursors = new EnumMap<>(CursorType.class);
    private Cursor currentCursor;
    private long previousCursorId;

    public CursorManager(CursorConfig config, MinecraftClient client) {
        this.config = config;
        this.client = client;

        for (CursorType type : CursorType.values()) {
            cursors.put(type, new Cursor((type)));
        }
    }

    public void loadCursorImage(CursorType type, BufferedImage image) throws IOException {
        Cursor cursor = cursors.get(type);
        cursor.loadImage(image, 1, 0, 0, true); // TODO: get values from config

        if (currentCursor == null) {
            return;
        }

        if (currentCursor.getType() == type) {
            reloadCursor();
        }
    }

    public void setCurrentCursor(CursorType type) {
        Cursor cursor = cursors.get(type);

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
