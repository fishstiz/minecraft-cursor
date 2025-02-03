package io.github.fishstiz.minecraftcursor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class CursorConfigLoader {
    public static final String FILE_EXTENSION = ".json";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final String path;
    private CursorConfig config;

    public CursorConfigLoader(InputStream stream) {
        this("", stream, false);
    }

    public CursorConfigLoader(String path) {
        this(path, null, true);
    }

    private CursorConfigLoader(String path, @Nullable InputStream stream, boolean createIfNotFound) {
        this.path = path;

        init(createIfNotFound, stream);
    }

    private void init(boolean createIfNotFound, @Nullable InputStream stream) {
        load(createIfNotFound, stream);

        if (config != null && config.get_hash() == null) {
            generateHash();
        }
    }

    public void load(boolean createIfNotFound, @Nullable InputStream stream) {
        try {
            if (stream != null) {
                config = MAPPER.readValue(stream, CursorConfig.class);
            } else if (!path.isEmpty()) {
                config = MAPPER.readValue(new File(path), CursorConfig.class);
            }
        } catch (FileNotFoundException e) {
            if (createIfNotFound) {
                MinecraftCursor.LOGGER.warn("Config not found, creating config file at: {}", path);
                createDefault();
            }
        } catch (IOException e) {
            config = new CursorConfig();
            MinecraftCursor.LOGGER.error("Failed to load file", e);
        }
    }

    public void createDefault() {
        CursorConfig defaultConfig = new CursorConfig();

        for (CursorType type : CursorType.types()) {
            defaultConfig.getOrCreateCursorSettings(type);
        }

        config = defaultConfig;
        save();
    }

    public void save() {
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(new File(path), config);
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Failed to save config file", e);
        }
    }

    public CursorConfig config() {
        return this.config;
    }

    // generate hash to check if resource has changed for cursor
    private void generateHash() {
        long hash = 0;
        long prime = 31;

        for (Map.Entry<String, CursorConfig.Settings> entry : config.settings.entrySet()) {
            String key = entry.getKey();
            CursorConfig.Settings value = entry.getValue();

            for (char c : key.toCharArray()) {
                hash = hash * prime + c;
            }

            hash = hash * prime + (long) value.getScale();
            hash = hash * prime + value.getXHot();
            hash = hash * prime + value.getYHot();
            hash = hash * prime + (value.getEnabled() ? 1 : 0);
        }

        config.set_hash(Long.toHexString(hash));
    }
}
