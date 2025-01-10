package io.github.fishstiz.minecraftcursor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CursorConfigLoader {
    public static final String FILE_EXTENSION = ".json";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final String path;
    private CursorConfig config;

    public CursorConfigLoader(String path) {
        this.path = path;
        load();
    }

    public void load() {
        try {
            config = MAPPER.readValue(new File(path), CursorConfig.class);
        } catch (FileNotFoundException e) {
            MinecraftCursor.LOGGER.warn("Config not found, creating config file at: {}", path);
            createDefault();
        } catch (IOException e) {
            config = new CursorConfig();
            MinecraftCursor.LOGGER.error("Failed to load file: ", e);
        }
    }

    public void save() {
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(new File(path), config);
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Failed to save config file: ", e);
        }
    }

    public void createDefault() {
        CursorConfig defaultConfig = new CursorConfig();

        for (CursorType type : CursorType.values()) {
            defaultConfig.createSettings(type);
        }

        config = defaultConfig;

        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(new File(path), config);
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Failed to save default config file: ", e);
        }
    }

    public CursorConfig config() {
        return this.config;
    }
}
