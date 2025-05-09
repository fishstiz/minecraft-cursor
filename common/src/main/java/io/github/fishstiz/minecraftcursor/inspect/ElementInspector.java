package io.github.fishstiz.minecraftcursor.inspect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;

public interface ElementInspector {
    default void destroy() {
        // no-op
    }

    default boolean isEnabled() {
        return false;
    }

    default boolean setHovered(GuiEventListener hovered, boolean cached) {
        return false;
    }

    default void renderDeepest(Minecraft minecraft, GuiGraphics guiGraphics, Screen screen, double mouseX, double mouseY) {
        // no-op
    }

    default void renderHovered(Minecraft minecraft, GuiGraphics guiGraphics) {
        // no-op
    }

    default void renderCacheSize(Minecraft minecraft, GuiGraphics guiGraphics) {
        // no-op
    }

    static ElementInspector toggle(ElementInspector elementInspector) {
        elementInspector.destroy();

        return elementInspector instanceof ElementInspectorImpl
                ? new ElementInspector() {} // no-op inspector
                : new ElementInspectorImpl();
    }
}
