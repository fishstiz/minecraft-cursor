package io.github.fishstiz.minecraftcursor.registry.utils;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import net.minecraft.client.gui.Element;

@FunctionalInterface
public interface ElementCursorTypeFunction {
    CursorType apply(Element element, double mouseX, double mouseY);
}
