package io.github.fishstiz.minecraftcursor.inspect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

public interface ElementInspector {
    default void destroy() {
        // no-op
    }

    default boolean setFocused(GuiEventListener hovered, boolean cached) {
        return false;
    }

    default void render(Minecraft minecraft, @NotNull Screen screen, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // no-op
    }

    static ElementInspector toggle(ElementInspector elementInspector) {
        elementInspector.destroy();

        return elementInspector instanceof ElementInspectorImpl
                ? new ElementInspector() {} // no-op inspector
                : new ElementInspectorImpl();
    }
}
