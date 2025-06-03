package io.github.fishstiz.minecraftcursor.cursor.resolver;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.fishstiz.minecraftcursor.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

final class ElementInspectorImpl implements ElementInspector {
    private static final Component SCREEN_LABEL = Component.literal("S: ").withColor(0xFF339BFF); // blue
    private static final Component CACHE_LABEL = Component.literal("Cache: ").withColor(0xFFFFFFFF); // white
    private static final Component DEEPEST_LABEL = Component.literal("D: ").withColor(0xFF00FF00); // green
    private static final Component PROCESSED_LABEL = Component.literal("P: ").withColor(0xFFFF0000); // red
    private static final float Z = 2000f;
    private static final float TEXT_SCALE = 0.75f;
    private HashSet<String> cache = new HashSet<>();
    private GuiEventListener processed;
    private String processedName;
    private boolean enabled = true;

    @Override
    public void destroy() {
        this.enabled = false;
        this.cache.clear();
        this.cache = null;
        this.processed = null;
        this.processedName = null;
    }

    @Override
    public boolean setProcessed(GuiEventListener processed, boolean cached) {
        this.processed = processed;
        this.processedName = getClassName(processed);
        if (cached) {
            this.cache.add(processedName);
        }
        return true;
    }

    @Override
    public void render(Minecraft minecraft, @NotNull Screen screen, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if (this.enabled) {
            ScreenRectangle screenRectangle = getBounds(screen);
            renderScreenName(minecraft, screen, screenRectangle, guiGraphics);
            renderCacheSize(minecraft, screenRectangle, guiGraphics);
            renderProcessed(minecraft, renderDeepest(minecraft, screen, guiGraphics, mouseX, mouseY), guiGraphics);
        }
    }

    private ScreenRectangle renderDeepest(Minecraft minecraft, Screen screen, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        GuiEventListener child = ElementWalker.findDeepest(screen, mouseX, mouseY);
        GuiEventListener inspect = child != null ? child : screen;
        ScreenRectangle bounds = getBounds(inspect);
        Component label = DEEPEST_LABEL.copy().append(getClassName(inspect));
        renderInfo(minecraft, guiGraphics, bounds, label, Position.TOP_LEFT, true);
        return bounds;
    }

    private void renderProcessed(Minecraft minecraft, ScreenRectangle container, GuiGraphics guiGraphics) {
        if (processed != null) {
            Component label = PROCESSED_LABEL.copy().append(processedName);
            ScreenRectangle bounds = getBounds(processed);
            int offsetY = bounds.top() != container.top() ? 0 : minecraft.font.lineHeight;

            renderInfo(minecraft, guiGraphics, bounds, label, Position.TOP_LEFT, offsetY, true);
        }
    }

    private void renderScreenName(Minecraft minecraft, Screen screen, ScreenRectangle bounds, GuiGraphics guiGraphics) {
        Component label = SCREEN_LABEL.copy().append(getClassName(screen));
        this.renderInfo(minecraft, guiGraphics, bounds, label, Position.BOTTOM_RIGHT, false);
    }

    private void renderCacheSize(Minecraft minecraft, ScreenRectangle screenBounds, GuiGraphics guiGraphics) {
        if (cache != null) {
            Component label = CACHE_LABEL.copy().append(String.valueOf(cache.size()));
            int offsetY = -minecraft.font.lineHeight;
            renderInfo(minecraft, guiGraphics, screenBounds, label, Position.BOTTOM_RIGHT, offsetY, false);
        }
    }

    private void renderInfo(Minecraft minecraft, GuiGraphics guiGraphics, ScreenRectangle bounds, Component label, Position pos, boolean outline) {
        this.renderInfo(minecraft, guiGraphics, bounds, label, pos, 0, outline);
    }

    private void renderInfo(Minecraft minecraft, GuiGraphics guiGraphics, ScreenRectangle bounds, Component label, Position pos, int offsetY, boolean outline) {
        TextColor textColor = label.getStyle().getColor();
        int color = textColor != null ? textColor.getValue() : 0xFFFFFFFF;

        int textWidth = minecraft.font.width(label);
        int textHeight = minecraft.font.lineHeight;
        int textX = pos.getX(bounds, textWidth, TEXT_SCALE, minecraft.getWindow().getGuiScaledWidth());
        int textY = pos.getY(bounds, offsetY, textHeight, TEXT_SCALE, minecraft.getWindow().getGuiScaledHeight());

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, 0, Z);

        if (outline) {
            guiGraphics.renderOutline(bounds.left(), bounds.top(), bounds.width(), bounds.height(), 0xFF000000 | color);
        }

        poseStack.scale(TEXT_SCALE, TEXT_SCALE, 0);
        guiGraphics.drawString(minecraft.font, label, (int) (textX / TEXT_SCALE), (int) (textY / TEXT_SCALE), color);

        poseStack.popPose();
    }

    private ScreenRectangle getBounds(@Nullable GuiEventListener element) {
        if (element instanceof LayoutElement layoutElement) {
            return layoutElement.getRectangle();
        }
        return element != null ? element.getRectangle() : ScreenRectangle.empty();
    }

    private String getClassName(GuiEventListener element) {
        String namespace = Services.PLATFORM.isDevelopmentEnvironment() ? "named" : "intermediary";
        return Services.PLATFORM.unmapClassName(namespace, element.getClass().getName());
    }

    public enum Position {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT;

        private static final int OFFSET = 2;

        public int getX(ScreenRectangle rect, int textWidth, float scale, int screenWidth) {
            int x = switch (this) {
                case TOP_LEFT, BOTTOM_LEFT -> rect.left() + OFFSET;
                case TOP_RIGHT, BOTTOM_RIGHT -> rect.left() + rect.width() - (int) (textWidth * scale);
            };

            if ((x + textWidth * scale) > screenWidth) {
                x = (int) (screenWidth - textWidth * scale);
                if (x < 0) x = 0;
            }

            return x;
        }

        public int getY(ScreenRectangle rect, int offsetY, int textHeight, float scale, int screenHeight) {
            return switch (this) {
                case TOP_LEFT, TOP_RIGHT -> rect.top() + OFFSET + offsetY;
                case BOTTOM_LEFT, BOTTOM_RIGHT -> {
                    if (rect.height() == 0 && rect.width() == 0) {
                        yield screenHeight - (int) (textHeight * scale) - OFFSET - offsetY;
                    }
                    yield rect.top() - OFFSET + Math.max(0, rect.height() - (int) (textHeight * scale)) + offsetY;
                }
            };
        }
    }
}
