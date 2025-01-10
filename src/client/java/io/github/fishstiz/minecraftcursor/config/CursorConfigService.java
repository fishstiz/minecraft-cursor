package io.github.fishstiz.minecraftcursor.config;

import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;

public class CursorConfigService {
    private final CursorConfigLoader loader;

    public CursorConfigService(String path) {
        if (!path.endsWith(CursorConfigLoader.FILE_EXTENSION)) {
            throw new IllegalArgumentException("File not supported. Must be: " + CursorConfigLoader.FILE_EXTENSION);
        }

        this.loader = new CursorConfigLoader(path);
    }

    public void saveSettings(Cursor cursor) {
        CursorConfig.Settings settings = new CursorConfig.Settings(cursor.getScale(), cursor.getXhot(), cursor.getYhot(), cursor.getEnabled());
        this.loader.config().updateSettings(cursor.getType(), settings);
    }

    public CursorConfig.Settings getSettings(CursorType type) {
        return this.loader.config().getSettings(type);
    }
}
