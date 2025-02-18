package io.github.fishstiz.minecraftcursor.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class DrawUtil {
    private DrawUtil() {
    }

    public static void drawScrollableTextLeftAlign(
            DrawContext context,
            TextRenderer textRenderer,
            Text text,
            int startX,
            int startY,
            int endX,
            int endY,
            int color
    ) {
        int textWidth = textRenderer.getWidth(text);
        int textY = (startY + endY - 9) / 2 + 1;
        int availableWidth = endX - startX;

        if (textWidth > availableWidth) {
            int overflowWidth = textWidth - availableWidth;
            double timeSeconds = Util.getMeasuringTimeMs() / 1000.0;
            double scrollDuration = Math.max(overflowWidth * 0.5, 3.0);
            double scrollFactor = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * timeSeconds / scrollDuration)) / 2.0 + 0.5;
            double scrollOffset = MathHelper.lerp(scrollFactor, 0.0, overflowWidth);

            context.enableScissor(startX, startY, endX, endY);
            context.drawTextWithShadow(textRenderer, text, startX - (int) scrollOffset, textY, color);
            context.disableScissor();
        } else {
            context.drawTextWithShadow(textRenderer, text, startX, textY, color);
        }
    }
}
