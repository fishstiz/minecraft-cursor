package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.client.StonecutterScreenAccessor;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.screen.StonecutterScreenHandler;

public class StonecutterScreenCursor extends HandledScreenCursor<StonecutterScreenHandler> {
    // Derived from StonecutterScreen#drawBackground
    public static final int RECIPES_OFFSET_X = 52;
    public static final int RECIPES_OFFSET_Y = 14;
    public static final int RECIPES_SCROLLOFFSET = 12;
    // Derived from StonecutterScreen#renderRecipeBackground
    public static final int GRID_SIZE = 4;
    public static final int RECIPE_SLOT_WIDTH = 16;
    public static final int RECIPE_SLOT_HEIGHT = 18;
    public static final int RECIPE_SLOT_HEIGHT_OFFSET = 2;

    private StonecutterScreenCursor() {
    }

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        cursorTypeRegistry.register(StonecutterScreen.class, new StonecutterScreenCursor()::getCursorType);
    }

    @Override
    protected CursorType getCursorType(Element element, double mouseX, double mouseY) {
        CursorType cursorType = super.getCursorType(element, mouseX, mouseY);
        if (cursorType != CursorType.DEFAULT) return cursorType;

        if (!MinecraftCursorClient.CONFIG.get().isStonecutterRecipesEnabled()) return CursorType.DEFAULT;

        StonecutterScreenAccessor stonecutterScreen = (StonecutterScreenAccessor) element;
        StonecutterScreenHandler handler = stonecutterScreen.getHandler();

        int recipesX = stonecutterScreen.getX() + RECIPES_OFFSET_X;
        int recipesY = stonecutterScreen.getY() + RECIPES_OFFSET_Y;
        int scrollOffset = stonecutterScreen.getScrollOffset();

        for (int i = scrollOffset; i < scrollOffset + RECIPES_SCROLLOFFSET && i < handler.getAvailableRecipeCount(); i++) {
            int recipeIndex = i - scrollOffset;
            int row = recipeIndex / GRID_SIZE;
            int slotX = recipesX + recipeIndex % GRID_SIZE * RECIPE_SLOT_WIDTH;
            int slotY = recipesY + row * RECIPE_SLOT_HEIGHT + RECIPE_SLOT_HEIGHT_OFFSET;

            boolean isRecipeHovered = mouseX >= slotX &&
                    mouseY >= slotY &&
                    mouseX < slotX + RECIPE_SLOT_WIDTH &&
                    mouseY < slotY + RECIPE_SLOT_HEIGHT;

            if (i != handler.getSelectedRecipe() && isRecipeHovered) {
                return CursorType.POINTER;
            }
        }
        return CursorType.DEFAULT;
    }
}
