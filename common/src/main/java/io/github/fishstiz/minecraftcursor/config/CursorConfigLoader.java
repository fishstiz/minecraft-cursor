package io.github.fishstiz.minecraftcursor.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.AnimationMode;

import java.io.*;

public class CursorConfigLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Gson GSON_ANIMATION = new GsonBuilder()
            .registerTypeAdapter(AnimatedCursorConfig.Frame.class, new AnimatedCursorConfig.FrameDeserializer())
            .registerTypeAdapter(AnimationMode.class, new AnimationMode.Deserializer())
            .create();

    private CursorConfigLoader() {
    }

    public static AnimatedCursorConfig getAnimationConfig(InputStream stream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(stream)) {
            return GSON_ANIMATION.fromJson(reader, AnimatedCursorConfig.class);
        }
    }

    public static CursorConfig.Resource loadResource(InputStream stream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(stream)) {
            return GSON.fromJson(reader, CursorConfig.Resource.class);
        }
    }

    public static CursorConfig load(File file) {
        CursorConfig config = new CursorConfig();

        try (FileReader reader = new FileReader(file)) {
            config = GSON.fromJson(reader, CursorConfig.class);
        } catch (FileNotFoundException e) {
            MinecraftCursor.LOGGER.info("[minecraft-cursor] Creating config file at '{}'...", file.getPath());
            saveConfig(file, config);
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("[minecraft-cursor] Failed to load config at '{}'", file.getPath());
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
