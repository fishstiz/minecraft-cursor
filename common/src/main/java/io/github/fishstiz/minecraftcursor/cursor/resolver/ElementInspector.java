package io.github.fishstiz.minecraftcursor.cursor.resolver;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface ElementInspector {
    ElementInspector NO_OP = new ElementInspector() {
    };

    static ResourceLocation getId() {
        return MinecraftCursor.loc("inspector");
    }

    default void destroy() {
    }

    default boolean setProcessed(GuiEventListener processed, boolean cached) {
        return false;
    }

    default void render(Minecraft minecraft, @NotNull Screen screen, GuiGraphics guiGraphics, double mouseX, double mouseY) {
    }

    default boolean isInspecting() {
        return false;
    }
}
