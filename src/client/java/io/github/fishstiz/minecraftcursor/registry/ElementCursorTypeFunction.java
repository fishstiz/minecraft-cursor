package io.github.fishstiz.minecraftcursor.registry;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.utils.QuadFunction;
import net.minecraft.client.gui.Element;

@FunctionalInterface
public interface ElementCursorTypeFunction extends QuadFunction<Element, Double, Double, Float, CursorType> {
}
