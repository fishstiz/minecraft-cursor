package io.github.fishstiz.minecraftcursor.cursor.resolver;

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
import org.joml.Matrix3x2fStack;

import java.util.HashSet;

final class ElementInspectorImpl implements ElementInspector {
    private static final float TEXT_SCALE = 0.75f;
    private final Component screenLabel = Component.literal("S: ").withColor(0xFF339BFF); // blue
    private final Component cacheLabel = Component.literal("Cache: ").withColor(0xFFFFFFFF); // white
    private final Component deepestLabel = Component.literal("D: ").withColor(0xFF00FF00); // green
    private final Component processedLabel = Component.literal("P: ").withColor(0xFFFF0000); // red
    private HashSet<String> cache = new HashSet<>();
    private Component cacheText = Component.empty();
    private GuiEventListener processed;
    private String processedName;
    private boolean enabled = true;

    @Override
    public boolean isInspecting() {
        return this.enabled;
    }

    @Override
    public void destroy() {
        this.enabled = false;
        this.cache.clear();
        this.cache = null;
        this.cacheText = null;
        this.processed = null;
        this.processedName = null;
    }

    @Override
    public boolean setProcessed(GuiEventListener processed, boolean cached) {
        this.processed = processed;
        this.processedName = getClassName(processed);
        if (cached) {
            this.cache.add(processedName);
            this.cacheText = cacheLabel.copy().append(String.valueOf(cache.size()));
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
        Component label = deepestLabel.copy().append(getClassName(inspect));
        renderInfo(minecraft, guiGraphics, bounds, label, Position.TOP_LEFT, true);
        return bounds;
    }

    private void renderProcessed(Minecraft minecraft, ScreenRectangle container, GuiGraphics guiGraphics) {
        if (processed != null) {
            Component label = processedLabel.copy().append(processedName);
            ScreenRectangle bounds = getBounds(processed);
            int index = bounds.top() != container.top() ? 0 : 1;
            this.renderInfo(minecraft, guiGraphics, bounds, label, Position.TOP_LEFT, index, true);
        }
    }

    private void renderScreenName(Minecraft minecraft, Screen screen, ScreenRectangle bounds, GuiGraphics guiGraphics) {
        Component label = screenLabel.copy().append(getClassName(screen));
        this.renderInfo(minecraft, guiGraphics, bounds, label, Position.BOTTOM_RIGHT, false);
    }

    private void renderCacheSize(Minecraft minecraft, ScreenRectangle screenBounds, GuiGraphics guiGraphics) {
        this.renderInfo(minecraft, guiGraphics, screenBounds, cacheText, Position.BOTTOM_RIGHT, -1, false);
    }

    private void renderInfo(Minecraft minecraft, GuiGraphics guiGraphics, ScreenRectangle bounds, Component label, Position pos, boolean outline) {
        this.renderInfo(minecraft, guiGraphics, bounds, label, pos, 0, outline);
    }

    private void renderInfo(Minecraft minecraft, GuiGraphics guiGraphics, ScreenRectangle bounds, Component label, Position pos, int index, boolean outline) {
        TextColor textColor = label.getStyle().getColor();
        int color = 0xFF000000 | (textColor != null ? textColor.getValue() : 0xFFFFFFFF);

        int textX = pos.getX(minecraft, bounds, label, TEXT_SCALE);
        int textY = pos.getY(minecraft, bounds, index, TEXT_SCALE);

        Matrix3x2fStack matrix3x2fStack = guiGraphics.pose().pushMatrix();
        guiGraphics.nextStratum();
        if (outline) guiGraphics.renderOutline(bounds.left(), bounds.top(), bounds.width(), bounds.height(), color);
        matrix3x2fStack.translate(TEXT_SCALE, TEXT_SCALE);
        matrix3x2fStack.scale(TEXT_SCALE, TEXT_SCALE);
        guiGraphics.drawString(minecraft.font, label, textX, textY, color);
        matrix3x2fStack.popMatrix();
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

        private static final int PADDING = 2;

        public int getX(Minecraft minecraft, ScreenRectangle bounds, Component text, float scale) {
            int screenWidth = minecraft.getWindow().getGuiScaledWidth();
            int textWidth = minecraft.font.width(text);

            int x = switch (this) {
                case TOP_LEFT, BOTTOM_LEFT -> (int) ((bounds.left() + PADDING) / scale);
                case TOP_RIGHT, BOTTOM_RIGHT -> {
                    int unscaledX = bounds.left() + bounds.width() - (int) (textWidth * scale) - PADDING;
                    yield (int) (unscaledX / scale);
                }
            };

            if ((x + textWidth) * scale > screenWidth) {
                x = (int) ((screenWidth - textWidth * scale - PADDING) / scale);
                if (x < 0) x = 0;
            }

            return x;
        }

        public int getY(Minecraft minecraft, ScreenRectangle bounds, int index, float scale) {
            int screenHeight = minecraft.getWindow().getGuiScaledHeight();
            int textHeight = minecraft.font.lineHeight;
            int offsetY = index * textHeight;

            return switch (this) {
                case TOP_LEFT, TOP_RIGHT -> (int) ((bounds.top() + PADDING + offsetY) / scale);
                case BOTTOM_LEFT, BOTTOM_RIGHT -> {
                    if (bounds.height() == 0 && bounds.width() == 0) {
                        yield (int) ((screenHeight - textHeight - PADDING - offsetY) / scale);
                    }
                    yield (int) ((bounds.top() - PADDING + Math.max(0, bounds.height() - textHeight) + offsetY) / scale);
                }
            };
        }
    }
}
