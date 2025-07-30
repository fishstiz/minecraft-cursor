package io.github.fishstiz.testmod.gui.components;

import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.testmod.compat.minecraftcursor.MinecraftCursorUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

// CursorController can be used if the element is not part of the screen's element tree.
public class DetachedElement extends Button {
    private final CursorType cursorType;

    public DetachedElement(CursorType cursorType) {
        super(
                0,
                0,
                DEFAULT_WIDTH,
                DEFAULT_HEIGHT,
                Component.literal("Detached ").append(MinecraftCursorUtil.getTranslation(cursorType)),
                Buttons::stub,
                DEFAULT_NARRATION
        );

        this.cursorType = cursorType;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        if (this.isHovered()) {
            CursorController.getInstance().setSingleCycleCursor(this.cursorType);
        }
    }
}
