package io.github.fishstiz.minecraftcursor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CursorConfigLoader {
    static final ObjectMapper MAPPER = new ObjectMapper();

    private CursorConfigLoader() {
    }

    public static CursorConfig fromStream(InputStream stream) throws IOException {
        CursorConfig config;
        config = MAPPER.readValue(stream, CursorConfig.class);
        return config;
    }

    public static CursorConfig fromFile(File file) {
        if (!file.getPath().endsWith(".json")) {
            throw new IllegalArgumentException("Unsupported file type.");
        }

        CursorConfig config = new CursorConfig();

        try {
            config = MAPPER.readValue(file, CursorConfig.class);
        } catch (FileNotFoundException e) {
            MinecraftCursor.LOGGER.info("Creating cursor config file at {}", file.getPath());
            saveConfig(file, config);
        } catch (IOException e) {
            MinecraftCursor.LOGGER.warn("Failed to load cursor config at {}", file.getPath());
        }

        config.file = file;
        return config;
    }

    public static void saveConfig(File file, CursorConfig config) {
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, config);
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Failed to save config file at {}", file.getPath());
        }
    }
}
