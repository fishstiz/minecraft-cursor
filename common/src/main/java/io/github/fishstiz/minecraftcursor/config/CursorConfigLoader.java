package io.github.fishstiz.minecraftcursor.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.AnimationMode;

import java.io.*;

public class CursorConfigLoader {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(AnimatedCursorConfig.Frame.class, new AnimatedCursorConfig.FrameDeserializer())
            .registerTypeAdapter(AnimationMode.class, new AnimationMode.Deserializer())
            .setPrettyPrinting()
            .create();

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
        CursorConfig config = new CursorConfig();

        try (FileReader reader = new FileReader(file)) {
            config = GSON.fromJson(reader, CursorConfig.class);
        } catch (FileNotFoundException e) {
            MinecraftCursor.LOGGER.info("[minecraft-cursor] Creating cursor config file at '{}'...", file.getPath());
            saveConfig(file, config);
        } catch (IOException e) {
            MinecraftCursor.LOGGER.warn("[minecraft-cursor] Failed to load cursor config at '{}'", file.getPath());
        }

        config.file = file;
        return config;
    }

    public static void saveConfig(File file, CursorConfig config) {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("[minecraft-cursor] Failed to save config file at '{}'", file.getPath());
        }
    }
}
