package io.github.fishstiz.minecraftcursor.cursor;

import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.api.CursorType;

public interface CursorProviderInternal extends CursorProvider {
    CursorType minecraftcursor$getCursorType(double mouseX, double mouseY);

    @Override
    default CursorType getCursorType(double mouseX, double mouseY) {
        return this.minecraftcursor$getCursorType(mouseX, mouseY);
    }
}
