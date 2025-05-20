package io.github.fishstiz.minecraftcursor.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.AnimationMode;

import java.io.*;

public class ConfigLoader {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(AnimationData.Frame.class, new AnimationData.FrameDeserializer())
            .registerTypeAdapter(AnimationMode.class, new AnimationMode.Deserializer())
            .setPrettyPrinting()
            .create();

    private ConfigLoader() {
    }

    public static AnimationData getAnimationConfig(InputStream stream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(stream)) {
            return GSON.fromJson(reader, AnimationData.class);
        }
    }

    public static Config.Resource loadResource(InputStream stream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(stream)) {
            return GSON.fromJson(reader, Config.Resource.class);
        }
    }

    public static Config load(File file) {
        Config config = new Config();

        try (FileReader reader = new FileReader(file)) {
            config = GSON.fromJson(reader, Config.class);
        } catch (FileNotFoundException e) {
            MinecraftCursor.LOGGER.info("[minecraft-cursor] Creating config file at '{}'...", file.getPath());
            saveConfig(file, config);
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("[minecraft-cursor] Failed to load config at '{}'", file.getPath());
        }

        config.file = file;
        return config;
    }

    public static void saveConfig(File file, Config config) {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("[minecraft-cursor] Failed to save config file at '{}'", file.getPath());
        }
    }
}
