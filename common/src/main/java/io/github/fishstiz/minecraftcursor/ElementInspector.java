package io.github.fishstiz.minecraftcursor;

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

import java.util.Optional;

class ElementInspector {
    private static final int DEEPEST_COLOR = 0xFF00FF00; // green
    private static final int HOVERED_COLOR = 0xFFFF0000; // red
    private static final float Z = 900f;
    private static final float TEXT_SCALE = 0.75f;
    private GuiEventListener hovered;
    private String hoveredName;
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public boolean setEnabled(boolean enabled) {
        this.enabled = enabled;
        return enabled;
    }

    public boolean setHovered(GuiEventListener hovered) {
        if (enabled) {
            this.hovered = hovered;
            this.hoveredName = getClassName(hovered);
            return true;
        }
        return false;
    }

    public void renderDeepest(Minecraft minecraft, GuiGraphics guiGraphics, Screen screen, double mouseX, double mouseY) {
        if (enabled) {
            GuiEventListener child = findDeepestChild(screen, mouseX, mouseY);
            GuiEventListener inspect = child != null ? child : screen;
            renderElement(guiGraphics, minecraft, inspect, DEEPEST_COLOR, getClassName(inspect), true);
        }
    }

    public void renderHovered(Minecraft minecraft, GuiGraphics guiGraphics) {
        if (enabled && hovered != null) {
            renderElement(guiGraphics, minecraft, hovered, HOVERED_COLOR, hoveredName, false);
        }
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
        int textX = rect.left();

        if ((textX + textWidth * TEXT_SCALE) > screenWidth) {
            textX = (int) (screenWidth - textWidth * TEXT_SCALE);
            if (textX < 0) textX = 0;
        }

        int textY = rect.top();
        if (bottomLabel) {
            textY = rect.top() + rect.height() - (int) (font.lineHeight * TEXT_SCALE);
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
        return Services.PLATFORM.unmapClassName("named", element.getClass().getName());
    }

    private @Nullable GuiEventListener findDeepestChild(ContainerEventHandler parent, double mouseX, double mouseY) {
        Optional<GuiEventListener> child = parent.getChildAt(mouseX, mouseY);
        if (child.isPresent()) {
            GuiEventListener listener = child.get();
            if (listener instanceof ContainerEventHandler nestedParent) {
                GuiEventListener deepChild = findDeepestChild(nestedParent, mouseX, mouseY);
                return (deepChild != null) ? deepChild : listener;
            }
            return listener;
        }
        return null;
    }
}
