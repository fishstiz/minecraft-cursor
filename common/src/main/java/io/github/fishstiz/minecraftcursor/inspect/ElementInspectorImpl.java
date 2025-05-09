package io.github.fishstiz.minecraftcursor.inspect;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.fishstiz.minecraftcursor.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;

public class ElementInspectorImpl implements ElementInspector {
    private static final int DEEPEST_COLOR = 0xFF00FF00; // green
    private static final int HOVERED_COLOR = 0xFFFF0000; // red
    private static final float Z = 900f;
    private static final float TEXT_SCALE = 0.75f;
    private HashSet<String> cache = new HashSet<>();
    private GuiEventListener hovered;
    private String hoveredName;

    @Override
    public void destroy() {
        this.cache.clear();
        this.cache = null;
        this.hovered = null;
        this.hoveredName = null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean setHovered(GuiEventListener hovered, boolean cached) {
        this.hovered = hovered;
        this.hoveredName = getClassName(hovered);
        if (cached) {
            this.cache.add(hoveredName);
        }
        return true;
    }

    @Override
    public void renderDeepest(Minecraft minecraft, GuiGraphics guiGraphics, Screen screen, double mouseX, double mouseY) {
        GuiEventListener child = findDeepestChild(screen, mouseX, mouseY);
        GuiEventListener inspect = child != null ? child : screen;
        renderElement(guiGraphics, minecraft, inspect, DEEPEST_COLOR, getClassName(inspect), true);
    }

    @Override
    public void renderHovered(Minecraft minecraft, GuiGraphics guiGraphics) {
        if (hovered != null) {
            renderElement(guiGraphics, minecraft, hovered, HOVERED_COLOR, hoveredName, false);
        }
    }

    @Override
    public void renderCacheSize(Minecraft minecraft, GuiGraphics guiGraphics) {
        String sizeString = "Cache Size: " + cache.size();
        Font font = minecraft.font;

        int textWidth = font.width(sizeString);
        int textHeight = font.lineHeight;

        float scale = TEXT_SCALE;
        int x = (int) (minecraft.getWindow().getGuiScaledWidth() - textWidth * scale - 1);
        int y = (int) (minecraft.getWindow().getGuiScaledHeight() - textHeight * scale - 1);

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, 0, Z);
        poseStack.scale(scale, scale, 0);

        guiGraphics.drawString(font, sizeString, (int) (x / scale), (int) (y / scale), 0xFFFFFFFF);

        poseStack.popPose();
    }

    private void renderElement(GuiGraphics guiGraphics, Minecraft minecraft, GuiEventListener element, int color, String label, boolean bottomLabel) {
        ScreenRectangle rect = getBounds(element);
        PoseStack poseStack = guiGraphics.pose();
        Font font = minecraft.font;

        poseStack.pushPose();
        poseStack.translate(0, 0, Z);
        guiGraphics.renderOutline(rect.left(), rect.top(), rect.width(), rect.height(), color);

        int textWidth = font.width(label);
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int textX = rect.left() + 1;

        if ((textX + textWidth * TEXT_SCALE) > screenWidth) {
            textX = (int) (screenWidth - textWidth * TEXT_SCALE);
            if (textX < 0) textX = 0;
        }

        int textY = rect.top() + 1;
        if (bottomLabel) {
            if (rect.height() == 0 && rect.width() == 0) {
                textY = minecraft.getWindow().getGuiScaledHeight() - (int) (font.lineHeight * TEXT_SCALE) - 1;
            } else {
                textY = rect.top() - 1 + Math.max(0, rect.height() - (int) (font.lineHeight * TEXT_SCALE));
            }
        }

        poseStack.scale(TEXT_SCALE, TEXT_SCALE, 0);
        guiGraphics.drawString(font, label, (int) (textX / TEXT_SCALE), (int) (textY / TEXT_SCALE), color);
        poseStack.popPose();
    }

    private ScreenRectangle getBounds(GuiEventListener element) {
        if (element instanceof LayoutElement le) {
            return new ScreenRectangle(le.getX(), le.getY(), le.getWidth(), le.getHeight());
        }
        return element.getRectangle();
    }

    private String getClassName(GuiEventListener element) {
        String namespace = Services.PLATFORM.isDevelopmentEnvironment() ? "named" : "intermediary";
        return Services.PLATFORM.unmapClassName(namespace, element.getClass().getName());
    }

    private @Nullable GuiEventListener findDeepestChild(ContainerEventHandler parent, double mouseX, double mouseY) {
        Optional<GuiEventListener> child = parent.getChildAt(mouseX, mouseY);
        if (child.isPresent()) {
            GuiEventListener hoveredElement = child.get();
            if (hoveredElement instanceof ContainerEventHandler nestedParent) {
                GuiEventListener deepChild = findDeepestChild(nestedParent, mouseX, mouseY);
                return (deepChild != null) ? deepChild : hoveredElement;
            }
            return hoveredElement;
        }
        return null;
    }
}
