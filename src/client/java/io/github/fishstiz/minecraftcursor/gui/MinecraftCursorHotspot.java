package io.github.fishstiz.minecraftcursor.gui;

import io.github.fishstiz.minecraftcursor.MinecraftCursorHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MinecraftCursorHotspot {
    public enum Color {
        RED(0xFFFF0000, "minecraft-cursor.options.hot.color.red", Formatting.RED),
        GREEN(0xFF00FF00, "minecraft-cursor.options.hot.color.green", Formatting.GREEN),
        BLUE(0xFF0000FF, "minecraft-cursor.options.hot.color.blue", Formatting.BLUE),
        BLACK(0xFF000000, "minecraft-cursor.options.hot.color.black", Formatting.RESET),
        WHITE(0xFFFFFFFF, "minecraft-cursor.options.hot.color.white", Formatting.RESET);

        public final int hex;
        public final String key;
        public final Formatting formatting;
        int ordinal;
        final static Color[] values = Color.values();
        static {
            int counter = 1;
            for (Color color: values)
                color.ordinal = counter++;
        }

        Color(int hex, String key, Formatting formatting) {
            this.hex = hex;
            this.key = key;
            this.formatting = formatting;
        }

        public Text getText() {
            return Text.translatable(this.key).formatted(this.formatting);
        }

        public static Color getColor(int ordinal) {
            return values[ordinal - 1];
        }
    }

    private final MinecraftCursorHandler cursorHandler;
    private Color color = Color.RED;

    public MinecraftCursorHotspot(MinecraftCursorHandler cursorHandler) {
        this.cursorHandler = cursorHandler;
    }

    public void render(DrawContext context, int mouseX, int mouseY, int scale) {
        int cursorWidth = this.cursorHandler.getWidth() * scale;
        int cursorHeight = this.cursorHandler.getHeight() * scale;
        int x1 = mouseX - cursorWidth / 2;
        int x2 = mouseX + cursorWidth / 2;
        int y1 = mouseY - cursorHeight / 2;
        int y2 = mouseY + cursorHeight / 2;

        context.drawHorizontalLine(x1, x2, mouseY, this.color.hex);
        context.drawVerticalLine(mouseX, y1, y2, this.color.hex);
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
