package io.github.fishstiz.minecraftcursor.registry.screen;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.ScreenCursorRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;

public class RecipeBookScreenCursorRegistry {
    private boolean isSearchFieldHovered;
    private boolean isToggleCraftableButtonHovered;
    private boolean isUnselectedTabHovered;

    private boolean isRecipeResultHovered;
    private boolean isPageButtonHovered;
    private boolean isRecipeAlternativesVisible;
    private boolean isRecipeAlternativeHovered;
//    private


    public RecipeBookScreenCursorRegistry(ScreenCursorRegistry cursorRegistry) {
        cursorRegistry.register(RecipeBookScreen.class, this::getCursorType);
    }

    private CursorType getCursorType(Element element, double mouseX, double mouseY, float delta) {
        if (isRecipeAlternativesVisible) {
            if (isRecipeAlternativeHovered) {
                return CursorType.POINTER;
            }
            return CursorType.DEFAULT;
        }

        if (isSearchFieldHovered) {
            return CursorType.TEXT;
        }

        if (isToggleCraftableButtonHovered || isUnselectedTabHovered || isRecipeResultHovered || isPageButtonHovered) {
            return CursorType.POINTER;
        }

        return CursorType.DEFAULT;
    }

    public void setSearchFieldHovered(boolean searchFieldHovered) {
        isSearchFieldHovered = searchFieldHovered;
    }

    public void setToggleCraftableButtonHovered(boolean toggleCraftableButtonHovered) {
        isToggleCraftableButtonHovered = toggleCraftableButtonHovered;
    }

    public void setUnselectedTabHovered(boolean unselectedTabHovered) {
        isUnselectedTabHovered = unselectedTabHovered;
    }

    public void setRecipeResultHovered(boolean recipeHovered) {
        isRecipeResultHovered = recipeHovered;
    }

    public void setPageButtonHovered(boolean pageButtonHovered) {
        isPageButtonHovered = pageButtonHovered;
    }

    public void setRecipeAlternativesVisible(boolean recipeAlternativesVisible) {
        isRecipeAlternativesVisible = recipeAlternativesVisible;
    }

    public void setRecipeAlternativeHovered(boolean recipeAlternativeHovered) {
        isRecipeAlternativeHovered = recipeAlternativeHovered;
    }
}
