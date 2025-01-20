package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.registry.utils.LookupUtils;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.LoomScreenHandler;

import java.lang.invoke.VarHandle;
import java.util.List;

public class LoomScreenCursor extends HandledScreenCursor {
    // Derived from LoomScreen.drawBackground()
    public static final int PATTERNS_OFFSET_X = 60;
    public static final int PATTERNS_OFFSET_Y = 13;
    public static final int GRID_SIZE = 4;
    public static final int PATTERN_SIZE = 14;

    private static final String CAN_APPLY_DYE_NAME = "field_2965";
    private static VarHandle canApplyDyePattern;
    private static final String VISIBLE_TOP_ROW_NAME = "field_39190";
    private static VarHandle visibleTopRow;

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        try {
            if (screenHandler == null || x == null || y == null) {
                throw new NoSuchFieldException();
            }
            initHandles();
            cursorTypeRegistry.register(LoomScreen.class, LoomScreenCursor::getCursorType);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            MinecraftCursor.LOGGER.warn("Could not register cursor type for LoomScreen");
        }
    }

    private static void initHandles() throws NoSuchFieldException, IllegalAccessException {
        Class<?> targetClass = LoomScreen.class;
        canApplyDyePattern = LookupUtils.getVarHandle(targetClass, CAN_APPLY_DYE_NAME, boolean.class);
        visibleTopRow = LookupUtils.getVarHandle(targetClass, VISIBLE_TOP_ROW_NAME, int.class);
    }

    public static CursorType getCursorType(Element element, double mouseX, double mouseY) {
        CursorType handledScreenCursor = HandledScreenCursor.getCursorType(element, mouseX, mouseY);
        if (handledScreenCursor != CursorType.DEFAULT) {
            return handledScreenCursor;
        }

        if (!MinecraftCursorClient.CONFIG.get().isLoomPatternsEnabled()) return CursorType.DEFAULT;

        LoomScreen loomScreen = (LoomScreen) element;

        if (!((boolean) canApplyDyePattern.get(loomScreen))) {
            return CursorType.DEFAULT;
        }

        LoomScreenHandler handler = (LoomScreenHandler) screenHandler.get(loomScreen);
        List<RegistryEntry<BannerPattern>> patterns = handler.getBannerPatterns();
        int patternsX = (int) x.get(loomScreen) + PATTERNS_OFFSET_X;
        int patternsY = (int) y.get(loomScreen) + PATTERNS_OFFSET_Y;

        exitLoop:
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int patternIndex = row + (int) visibleTopRow.get(loomScreen);
                int patternSlot = patternIndex * GRID_SIZE + col;
                if (patternSlot >= patterns.size()) {
                    break exitLoop;
                }

                int patternX = patternsX + col * PATTERN_SIZE;
                int patternY = patternsY + row * PATTERN_SIZE;
                boolean isPointWithinBounds = mouseX >= patternX &&
                        mouseY >= patternY &&
                        mouseX < patternX + PATTERN_SIZE &&
                        mouseY < patternY + PATTERN_SIZE;

                if (isPointWithinBounds) {
                    return CursorType.POINTER;
                }
            }
        }
        return CursorType.DEFAULT;
    }
}
