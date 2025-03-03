package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.access.LoomScreenAccessor;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.List;

public class LoomScreenCursorHandler extends HandledScreenCursorHandler<LoomMenu, LoomScreen> {
    // Derived from LoomScreen#drawBackground
    public static final int PATTERNS_OFFSET_X = 60;
    public static final int PATTERNS_OFFSET_Y = 13;
    public static final int GRID_SIZE = 4;
    public static final int PATTERN_SIZE = 14;

    @Override
    public CursorType getCursorType(LoomScreen loomScreen, double mouseX, double mouseY) {
        CursorType cursorType = super.getCursorType(loomScreen, mouseX, mouseY);
        if (cursorType != CursorType.DEFAULT) return cursorType;

        if (!MinecraftCursor.CONFIG.isLoomPatternsEnabled()) return CursorType.DEFAULT;

        LoomScreenAccessor loomScreenAccessor = (LoomScreenAccessor) loomScreen;

        if (!loomScreenAccessor.getCanApplyDyePattern()) return CursorType.DEFAULT;

        LoomMenu handler = loomScreenAccessor.getHandler();
        List<Holder<BannerPattern>> patterns = handler.getSelectablePatterns();
        int patternsX = loomScreenAccessor.getX() + PATTERNS_OFFSET_X;
        int patternsY = loomScreenAccessor.getY() + PATTERNS_OFFSET_Y;

        grid:
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int patternIndex = row + loomScreenAccessor.getVisibleTopRow();
                int patternSlot = patternIndex * GRID_SIZE + col;
                if (patternSlot >= patterns.size()) {
                    break grid;
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
