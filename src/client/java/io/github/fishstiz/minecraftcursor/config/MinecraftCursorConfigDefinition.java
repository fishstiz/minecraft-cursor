package io.github.fishstiz.minecraftcursor.config;

public class MinecraftCursorConfigDefinition<T> {
    public static final MinecraftCursorConfigDefinition<Boolean> ENABLED = new MinecraftCursorConfigDefinition<>("enabled", true, Boolean.class);
    public static final MinecraftCursorConfigDefinition<Double> SCALE = new MinecraftCursorConfigDefinition<>("scale", 1.00, Double.class);
    public static final MinecraftCursorConfigDefinition<Integer> X_HOTSPOT = new MinecraftCursorConfigDefinition<>("xhot", 0, Integer.class);
    public static final MinecraftCursorConfigDefinition<Integer> Y_HOTSPOT = new MinecraftCursorConfigDefinition<>("yhot", 0, Integer.class);

    public final String key;
    public final T defaultValue;
    public final Class<T> type;

    private MinecraftCursorConfigDefinition(String key, T defaultValue, Class<T> type) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.type = type;
    }
}

