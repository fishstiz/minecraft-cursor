package io.github.fishstiz.minecraftcursor.util;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class DrawUtil {
    private DrawUtil() {
    }

    public static void drawScrollableTextLeftAlign(
            GuiGraphics context,
            Font textRenderer,
            Component text,
            int startX,
            int startY,
            int endX,
            int endY,
            int color
    ) {
        int textWidth = textRenderer.width(text);
        int textY = (startY + endY - 9) / 2 + 1;
        int availableWidth = endX - startX;

        if (textWidth > availableWidth) {
            int overflowWidth = textWidth - availableWidth;
            double timeSeconds = Util.getMillis() / 1000.0;
            double scrollDuration = Math.max(overflowWidth * 0.5, 3.0);
            double scrollFactor = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * timeSeconds / scrollDuration)) / 2.0 + 0.5;
            double scrollOffset = Mth.lerp(scrollFactor, 0.0, overflowWidth);

            context.enableScissor(startX, startY, endX, endY);
            context.drawString(textRenderer, text, startX - (int) scrollOffset, textY, color);
            context.disableScissor();
        } else {
            context.drawString(textRenderer, text, startX, textY, color);
        }
    }

    public static void drawCheckerboard(
            GuiGraphics guiGraphics,
            int x,
            int y,
            int width,
            int height,
            float cellSize,
            ResourceLocation checkerboard,
            int textureSize
    ) {
        float drawWidth = textureSize * cellSize;
        float drawHeight = textureSize * cellSize;

        float clippedWidth = Math.min(drawWidth, width);
        float clippedHeight = Math.min(drawHeight, height);

        float uRatio = clippedWidth / drawWidth;
        float vRatio = clippedHeight / drawHeight;

        int uvWidth = Math.round(textureSize * uRatio);
        int uvHeight = Math.round(textureSize * vRatio);

        guiGraphics.blit(
                RenderType::guiTextured,
                checkerboard,
                x, y,
                0, 0,
                (int) clippedWidth, (int) clippedHeight,
                uvWidth, uvHeight,
                textureSize, textureSize
        );
    }
}
