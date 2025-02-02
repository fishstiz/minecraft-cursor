package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.client.access.LoomScreenAccessor;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.LoomScreenHandler;

import java.util.List;

public class LoomScreenCursorHandler extends HandledScreenCursorHandler<LoomScreenHandler, LoomScreen> {
    // Derived from LoomScreen#drawBackground
    public static final int PATTERNS_OFFSET_X = 60;
    public static final int PATTERNS_OFFSET_Y = 13;
    public static final int GRID_SIZE = 4;
    public static final int PATTERN_SIZE = 14;

    @Override
    public CursorType getCursorType(LoomScreen loomScreen, double mouseX, double mouseY) {
        CursorType cursorType = super.getCursorType(loomScreen, mouseX, mouseY);
        if (cursorType != CursorType.DEFAULT) return cursorType;

        if (!MinecraftCursorClient.CONFIG.get().isLoomPatternsEnabled()) return CursorType.DEFAULT;

        LoomScreenAccessor loomScreenAccessor = (LoomScreenAccessor) loomScreen;

        if (!loomScreenAccessor.getCanApplyDyePattern()) return CursorType.DEFAULT;

        LoomScreenHandler handler = loomScreenAccessor.getHandler();
        List<RegistryEntry<BannerPattern>> patterns = handler.getBannerPatterns();
        int patternsX = loomScreenAccessor.getX() + PATTERNS_OFFSET_X;
        int patternsY = loomScreenAccessor.getY() + PATTERNS_OFFSET_Y;

        exitLoop:
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int patternIndex = row + loomScreenAccessor.getVisibleTopRow();
                int patternSlot = patternIndex * GRID_SIZE + col;
                if (patternSlot >= patterns.size()) {
                    break exitLoop;
                }
                int patternX = patternsX + col * PATTERN_SIZE;
                int patternY = patternsY + row * PATTERN_SIZE;
                if (mouseX >= patternX
                        && mouseY >= patternY
                        && mouseX < patternX + PATTERN_SIZE
                        && mouseY < patternY + PATTERN_SIZE) {
                    return CursorType.POINTER;
                }
            }
        }
        return CursorType.DEFAULT;
    }
}
