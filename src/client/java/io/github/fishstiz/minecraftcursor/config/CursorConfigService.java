package io.github.fishstiz.minecraftcursor.config;

import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;

public class CursorConfigService {
    private final CursorConfigLoader loader;

    public CursorConfigService(String path) {
        assert path.endsWith(CursorConfigLoader.FILE_EXTENSION)
                : "File not supported. Must be: " + CursorConfigLoader.FILE_EXTENSION;

        this.loader = new CursorConfigLoader(path);
    }

    public void saveSettings(Cursor cursor) {
        CursorConfig.Settings settings = new CursorConfig.Settings(cursor.getScale(), cursor.getXhot(), cursor.getYhot(), cursor.getEnabled());

        if (hasChanges(loader.config().getCursorSettings(cursor.getType()), settings)) {
            this.loader.config().updateCursorSettings(cursor.getType(), settings);
            this.loader.save();
        }
    }

    public CursorConfig.Settings getSettings(CursorType type) {
        return this.loader.config().getCursorSettings(type);
    }

    public static boolean hasChanges(CursorConfig.Settings oldSettings, CursorConfig.Settings newSettings) {
        boolean isChanged = false;

        isChanged |= oldSettings.getEnabled() != newSettings.getEnabled();
        isChanged |= oldSettings.getScale() != newSettings.getScale();
        isChanged |= oldSettings.getXHot() != newSettings.getXHot();
        isChanged |= oldSettings.getYHot() != newSettings.getYHot();

        return isChanged;
    }
}
