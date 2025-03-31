package io.github.fishstiz.minecraftcursor.config.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

class MixinConfigProperties {
    private final Logger logger;
    private final Property<Boolean> ignoreModCheckGlfw = new Property<>("ignore_mod_check_glfw", false);

    private static class Property<E> {
        private final String name;
        private E value;

        private Property(String name, E value) {
            this.name = name;
            this.value = value;
        }

        public String asProperty() {
            return String.valueOf(value);
        }
    }

    MixinConfigProperties(Logger logger) {
        this.logger = logger;

        Properties properties = new Properties();
        File file = FabricLoader.getInstance().getConfigDir().resolve("minecraft-cursor.properties").toFile();

        if (!file.exists()) {
            createDefault(file, properties);
            return;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
            ignoreModCheckGlfw.value = Boolean.parseBoolean(properties.getProperty(ignoreModCheckGlfw.name, ignoreModCheckGlfw.asProperty()));
        } catch (IOException e) {
            this.logger.error("[minecraft-cursor] Could not load properties file at '{}'", file);
        }
    }

    private void createDefault(File file, Properties properties) {
        try {
            boolean isCreated = file.createNewFile();
            if (!isCreated) return;

            logger.info("[minecraft-cursor] Creating properties file at '{}'", file);

            properties.setProperty(ignoreModCheckGlfw.name, ignoreModCheckGlfw.asProperty());

            try (FileOutputStream fos = new FileOutputStream(file)) {
                properties.store(fos, "Minecraft Cursor Mixins Configuration");
            }
        } catch (IOException e) {
            logger.error("[minecraft-cursor] Failed to create properties file at '{}'", file);
        }
    }

    public boolean ignoreModCheckGlfw() {
        return ignoreModCheckGlfw.value;
    }
}
