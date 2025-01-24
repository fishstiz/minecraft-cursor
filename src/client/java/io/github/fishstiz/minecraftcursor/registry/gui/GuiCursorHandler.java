package io.github.fishstiz.minecraftcursor.registry.gui;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import net.minecraft.client.gui.Element;

public abstract class GuiCursorHandler {
    protected GuiCursorHandler() {
    }

    abstract protected CursorType getCursorType(Element element, double mouseX, double mouseY);
}
