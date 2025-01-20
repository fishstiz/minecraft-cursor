package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.registry.utils.LookupUtils;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.screen.StonecutterScreenHandler;

import java.lang.invoke.VarHandle;

public class StonecutterScreenCursor extends HandledScreenCursor {
    // Derived from StonecutterScreen.drawBackground()
    public static final int RECIPES_OFFSET_X = 52;
    public static final int RECIPES_OFFSET_Y = 14;
    public static final int RECIPES_SCROLLOFFSET = 12;
    // Derived from StonecutterScreen.renderRecipeBackground()
    public static final int GRID_SIZE = 4;
    public static final int RECIPE_SLOT_WIDTH = 16;
    public static final int RECIPE_SLOT_HEIGHT = 18;
    public static final int RECIPE_SLOT_HEIGHT_OFFSET = 2;

    private static final String SCROLL_OFFSET_NAME = "field_17671";
    private static VarHandle scrollOffset;

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        try {
            if (screenHandler == null || x == null || y == null) {
                throw new NoSuchFieldException();
            }
            initHandles();
            cursorTypeRegistry.register(StonecutterScreen.class, StonecutterScreenCursor::getCursorType);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            MinecraftCursor.LOGGER.warn("Could not register cursor type for StonecutterScreen");
        }
    }

    private static void initHandles() throws NoSuchFieldException, IllegalAccessException {
        scrollOffset = LookupUtils.getVarHandle(StonecutterScreen.class, SCROLL_OFFSET_NAME, int.class);
    }

    public static CursorType getCursorType(Element element, double mouseX, double mouseY) {
        CursorType handledScreenCursor = HandledScreenCursor.getCursorType(element, mouseX, mouseY);
        if (handledScreenCursor != CursorType.DEFAULT) {
            return handledScreenCursor;
        }

        if (!MinecraftCursorClient.CONFIG.get().isStonecutterRecipesEnabled()) return CursorType.DEFAULT;

        StonecutterScreen stonecutterScreen = (StonecutterScreen) element;
        StonecutterScreenHandler handler = (StonecutterScreenHandler) screenHandler.get(stonecutterScreen);

        int recipesX = (int) x.get(stonecutterScreen) + RECIPES_OFFSET_X;
        int recipesY = (int) y.get(stonecutterScreen) + RECIPES_OFFSET_Y;
        int scrollOffset = (int) StonecutterScreenCursor.scrollOffset.get(stonecutterScreen);

        for (int i = scrollOffset; i < scrollOffset + RECIPES_SCROLLOFFSET && i < handler.getAvailableRecipeCount(); i++) {
            int recipeIndex = i - scrollOffset;
            int row = recipeIndex / GRID_SIZE;
            int slotX = recipesX + recipeIndex % GRID_SIZE * RECIPE_SLOT_WIDTH;
            int slotY = recipesY + row * RECIPE_SLOT_HEIGHT + RECIPE_SLOT_HEIGHT_OFFSET;

            boolean isPointWithinBounds = mouseX >= slotX &&
                    mouseY >= slotY &&
                    mouseX < slotX + RECIPE_SLOT_WIDTH &&
                    mouseY < slotY + RECIPE_SLOT_HEIGHT;

            if (i != handler.getSelectedRecipe() && isPointWithinBounds) {
                return CursorType.POINTER;
            }
        }
        return CursorType.DEFAULT;
    }
}
