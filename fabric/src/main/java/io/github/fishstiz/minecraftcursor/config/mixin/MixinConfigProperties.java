package io.github.fishstiz.minecraftcursor.config.mixin;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;

class MixinConfigProperties {
    final File file;
    private final Logger logger;
    private final List<KV<?>> store = new ArrayList<>();
    final KV<Boolean> ignoreModCheckGlfw = new KV<>("ignore_mod_check_glfw_mixin", false);
    final KV<Boolean> forceDisableGlfw = new KV<>("force_disable_glfw_mixin", false);

    MixinConfigProperties(Logger logger, Path directory) {
        this.logger = logger;
        this.file = directory.resolve("minecraft-cursor.properties").toFile();

        getProperties();
    }

    private String getComments() {
        return String.format("""
                         Minecraft Cursor Launch Properties
                         %s: Ignore mod check to apply GlfwMixin. May cause crash on mixin initialization with certain mods. Default: %s
                         %s: Forcefully disables GlfwMixin. May fix crash on mixin initialization. Default: %s
                        """,
                ignoreModCheckGlfw.name, ignoreModCheckGlfw.defaultValue,
                forceDisableGlfw.name, forceDisableGlfw.defaultValue
        );
    }

    private void getProperties() {
        Properties properties = new Properties();

        if (!file.exists()) {
            createDefault(properties);
            return;
        }

        boolean isStale = false;

        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);

            for (KV<?> kv : store) {
                if (!kv.setValuefromProperties(properties)) {
                    properties.setProperty(kv.name, String.valueOf(kv.defaultValue));
                    isStale = true;
                }
            }

        } catch (IOException e) {
            this.logger.error("[minecraft-cursor] Could not load properties file at '{}'", file);
        }

        if (isStale) saveProperties();
    }

    private void createDefault(Properties properties) {
        try {
            boolean isCreated = file.createNewFile();
            if (!isCreated) return;

            logger.info("[minecraft-cursor] Creating properties file at '{}'", file);

            for (KV<?> kv : store) {
                properties.setProperty(kv.name, String.valueOf(kv.defaultValue));
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                properties.store(fos, getComments());
            }
        } catch (IOException e) {
            logger.error("[minecraft-cursor] Failed to create properties file at '{}'", file);
        }
    }

    private void saveProperties() {
        Properties properties = new Properties();

        for (KV<?> kv : store) {
            properties.setProperty(kv.name, kv.getString());
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            properties.store(fos, getComments());
            logger.info("[minecraft-cursor] Updated properties file at '{}'", file);
        } catch (IOException e) {
            logger.error("[minecraft-cursor] Failed to update properties file at '{}'", file);
        }
    }

    class KV<E> {
        private final String name;
        private final Function<String, E> parser;
        private final @NotNull E defaultValue;
        private E value;

        private KV(String name, @NotNull E value) {
            this.name = Objects.requireNonNull(name);
            this.parser = getParser(Objects.requireNonNull(value));
            this.defaultValue = value;
            this.value = value;

            store.add(this);
        }

        public @NotNull String key() {
            return name;
        }

        public @NotNull E value() {
            return value != null ? value : defaultValue;
        }

        private boolean setValuefromProperties(Properties properties) {
            try {
                var parsedValue = parser.apply(properties.getProperty(name));
                this.value = parsedValue != null ? parsedValue : defaultValue;
                return parsedValue != null;
            } catch (Exception e) {
                logger.error("[minecraft-cursor] Failed to parse property '{}={}'. Using default: '{}'", name, value, defaultValue);
                this.value = defaultValue;
                return false;
            }
        }

        private String getString() {
            return String.valueOf(value != null ? value : defaultValue);
        }

        @SuppressWarnings("unchecked")
        private Function<String, E> getParser(E value) {
            if (value instanceof Integer) {
                return (Function<String, E>) (Function<String, Integer>) Integer::parseInt;
            } else if (value instanceof Double) {
                return (Function<String, E>) (Function<String, Double>) Double::parseDouble;
            } else if (value instanceof Boolean) {
                return (Function<String, E>) (Function<String, Boolean>) KV::parseBooleanOrNull;
            } else if (value instanceof Long) {
                return (Function<String, E>) (Function<String, Long>) Long::parseLong;
            } else if (value instanceof String) {
                return (Function<String, E>) Function.<String>identity();
            }

            throw new IllegalArgumentException("Unsupported property type: " + value.getClass());
        }

        private static Boolean parseBooleanOrNull(String s) {
            if ("true".equalsIgnoreCase(s)) return true;
            if ("false".equalsIgnoreCase(s)) return false;
            return null;
        }
    }
}
