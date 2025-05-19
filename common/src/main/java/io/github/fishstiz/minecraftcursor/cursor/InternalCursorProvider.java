package io.github.fishstiz.minecraftcursor.cursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import org.jetbrains.annotations.NotNull;

public interface InternalCursorProvider {
    @NotNull CursorType minecraft_cursor$getCursorType(double mouseX, double mouseY);
}
