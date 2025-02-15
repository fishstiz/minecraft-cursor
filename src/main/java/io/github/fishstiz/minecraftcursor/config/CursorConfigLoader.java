package io.github.fishstiz.minecraftcursor.config;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import org.spongepowered.include.com.google.gson.Gson;
import org.spongepowered.include.com.google.gson.GsonBuilder;

import java.io.*;

public class CursorConfigLoader {
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private CursorConfigLoader() {
    }

    public static AnimatedCursorConfig getAnimationConfig(InputStream stream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(stream)) {
            return GSON.fromJson(reader, AnimatedCursorConfig.class);
        }
    }

    public static CursorConfig fromStream(InputStream stream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(stream)) {
            return GSON.fromJson(reader, CursorConfig.class);
        }
    }

    public static CursorConfig fromFile(File file) {
        if (!file.getPath().endsWith(".json")) {
            throw new IllegalArgumentException("Unsupported file type.");
        }

        CursorConfig config = new CursorConfig();

        try (FileReader reader = new FileReader(file)) {
            config = GSON.fromJson(reader, CursorConfig.class);
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
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Failed to save config file at {}", file.getPath());
        }
    }
}
