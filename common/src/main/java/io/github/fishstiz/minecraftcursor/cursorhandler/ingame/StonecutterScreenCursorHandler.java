package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.access.StonecutterScreenAccessor;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.world.inventory.StonecutterMenu;

public class StonecutterScreenCursorHandler extends HandledScreenCursorHandler<StonecutterMenu, StonecutterScreen> {
    // Derived from StonecutterScreen#drawBackground
    public static final int RECIPES_OFFSET_X = 52;
    public static final int RECIPES_OFFSET_Y = 14;
    public static final int RECIPES_SCROLLOFFSET = 12;
    // Derived from StonecutterScreen#renderRecipeBackground
    public static final int GRID_SIZE = 4;
    public static final int RECIPE_SLOT_WIDTH = 16;
    public static final int RECIPE_SLOT_HEIGHT = 18;
    public static final int RECIPE_SLOT_HEIGHT_OFFSET = 2;

    @Override
    public CursorType getCursorType(StonecutterScreen stonecutterScreen, double mouseX, double mouseY) {
        CursorType cursorType = super.getCursorType(stonecutterScreen, mouseX, mouseY);
        if (cursorType != CursorType.DEFAULT) return cursorType;

        if (!MinecraftCursor.CONFIG.isStonecutterRecipesEnabled()) return CursorType.DEFAULT;

        StonecutterScreenAccessor accessor = (StonecutterScreenAccessor) stonecutterScreen;
        StonecutterMenu handler = accessor.getHandler();
        int recipesX = accessor.getX() + RECIPES_OFFSET_X;
        int recipesY = accessor.getY() + RECIPES_OFFSET_Y;
        int scrollOffset = accessor.getScrollOffset();

        for (int i = scrollOffset; i < scrollOffset + RECIPES_SCROLLOFFSET && i < handler.getNumberOfVisibleRecipes(); i++) {
            int recipeIndex = i - scrollOffset;
            int row = recipeIndex / GRID_SIZE;
            int slotX = recipesX + recipeIndex % GRID_SIZE * RECIPE_SLOT_WIDTH;
            int slotY = recipesY + row * RECIPE_SLOT_HEIGHT + RECIPE_SLOT_HEIGHT_OFFSET;

            boolean isRecipeHovered = mouseX >= slotX &&
                    mouseY >= slotY &&
                    mouseX < slotX + RECIPE_SLOT_WIDTH &&
                    mouseY < slotY + RECIPE_SLOT_HEIGHT;

            if (i != handler.getSelectedRecipeIndex() && isRecipeHovered) {
                return CursorType.POINTER;
            }
        }
        return CursorType.DEFAULT;
    }
}
