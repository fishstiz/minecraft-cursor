package io.github.fishstiz.minecraftcursor.config;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

public class MinecraftCursorConfigManager {
    private final File file;
    private boolean enabled = MinecraftCursorConfigDefinition.ENABLED.defaultValue;
    private double scale = MinecraftCursorConfigDefinition.SCALE.defaultValue;
    private int xhot = MinecraftCursorConfigDefinition.X_HOTSPOT.defaultValue;
    private int yhot = MinecraftCursorConfigDefinition.Y_HOTSPOT.defaultValue;

    public MinecraftCursorConfigManager(String modId) {
        this.file = new File(modId, "options.txt");

        if (!this.file.getParentFile().exists()) {
            boolean created = this.file.getParentFile().mkdir();
            if (!created) {
                MinecraftCursor.LOGGER.error("Failed to create parent directory of config file.");
            }
        }

        if (!this.file.exists()) {
            this.saveConfig();
        } else {
            this.loadConfig();
        }
    }

    public void saveConfig() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.file))) {
            writeConfigLine(writer, MinecraftCursorConfigDefinition.ENABLED.key, this.enabled);
            writeConfigLine(writer, MinecraftCursorConfigDefinition.SCALE.key, this.scale);
            writeConfigLine(writer, MinecraftCursorConfigDefinition.X_HOTSPOT.key, this.xhot);
            writeConfigLine(writer, MinecraftCursorConfigDefinition.Y_HOTSPOT.key, this.yhot);
            writer.flush();
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Error saving configuration file: {}", String.valueOf(e));
        }
    }

    public void loadConfig() {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("=");
                if (parts.length != 2) {
                    continue;
                }

                String key = parts[0].trim();
                String value = parts[1].trim();

                if (key.equals(MinecraftCursorConfigDefinition.ENABLED.key)) {
                    this.enabled = parseValue(value, MinecraftCursorConfigDefinition.ENABLED.type, MinecraftCursorConfigDefinition.ENABLED.defaultValue);
                } else if (key.equals(MinecraftCursorConfigDefinition.SCALE.key)) {
                    this.scale = parseValue(value, MinecraftCursorConfigDefinition.SCALE.type, MinecraftCursorConfigDefinition.SCALE.defaultValue);
                } else if (key.equals(MinecraftCursorConfigDefinition.X_HOTSPOT.key)) {
                    this.xhot = parseValue(value, MinecraftCursorConfigDefinition.X_HOTSPOT.type, MinecraftCursorConfigDefinition.X_HOTSPOT.defaultValue);
                } else if (key.equals(MinecraftCursorConfigDefinition.Y_HOTSPOT.key)) {
                    this.yhot = parseValue(value, MinecraftCursorConfigDefinition.Y_HOTSPOT.type, MinecraftCursorConfigDefinition.Y_HOTSPOT.defaultValue);
                }
            }
        } catch (FileNotFoundException e) {
            MinecraftCursor.LOGGER.error("Config file not found: {}", String.valueOf(e));
        } catch (IOException e) {
            MinecraftCursor.LOGGER.error("Error reading config file: {}", String.valueOf(e));
        }
    }

    private void writeConfigLine(BufferedWriter writer, String key, Object value) throws IOException {
        writer.write(key + "=" + value);
        writer.newLine();
    }

    private static <T> T parseValue(String value, Class<T> type, T fallbackValue) {
        if (value.isEmpty()) {
            return fallbackValue;
        }

        try {
            if (type == Integer.class) {
                return type.cast(Integer.valueOf(value));
            } else if (type == Double.class) {
                return type.cast(Double.valueOf(value));
            } else if (type == Boolean.class) {
                return type.cast(Boolean.valueOf(value));
            }
            return fallbackValue;
        } catch (NumberFormatException e) {
            MinecraftCursor.LOGGER.error("Incorrect value: {}. Default value used. Required type is {}", value, type);
            return fallbackValue;
        } catch (Exception e) {
            return fallbackValue;
        }
    }

    public int getYhot() {
        return yhot;
    }

    public void setYhot(int yhot) {
        this.yhot = yhot;
    }

    public int getXhot() {
        return xhot;
    }

    public void setXhot(int xhot) {
        this.xhot = xhot;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}