package io.github.fishstiz.minecraftcursor.cursor.handler.ingame;

import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.cursorhandler.access.RecipeAlternativesWidgetAccessor;
import io.github.fishstiz.minecraftcursor.mixin.cursorhandler.access.RecipeBookResultsAccessor;
import io.github.fishstiz.minecraftcursor.mixin.cursorhandler.access.RecipeBookScreenAccessor;
import io.github.fishstiz.minecraftcursor.mixin.cursorhandler.access.RecipeBookWidgetAccessor;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.world.inventory.RecipeBookMenu;

public class RecipeBookScreenCursorHandler implements CursorHandler<AbstractRecipeBookScreen<?>> {
    @Override
    public CursorType getCursorType(AbstractRecipeBookScreen<? extends RecipeBookMenu> recipeBookScreen, double mouseX, double mouseY) {
        RecipeBookWidgetAccessor recipeBook = (RecipeBookWidgetAccessor) ((RecipeBookScreenAccessor<?>) recipeBookScreen).getRecipeBook();
        if (!recipeBook.invokeIsOpen()) return CursorType.DEFAULT;

        RecipeBookResultsAccessor recipesArea = (RecipeBookResultsAccessor) recipeBook.getRecipesArea();
        RecipeAlternativesWidgetAccessor alternatesWidget = (RecipeAlternativesWidgetAccessor) recipesArea.getAlternatesWidget();

        if (((OverlayRecipeComponent) alternatesWidget).isVisible()) {
            return getAlternatesWidgetCursor(alternatesWidget);
        }

        boolean isResultHovered = recipesArea.getHoveredResultButton() != null;
        if (isResultHovered && CursorTypeUtil.canShift()) {
            return CursorType.SHIFT;
        } else if (isButtonHovered(recipeBook, recipesArea) || isResultHovered) {
            return CursorType.POINTER;
        } else if (recipeBook.getSearchField().isHovered()) {
            return CursorType.TEXT;
        }
        return getTabCursor(recipeBook);
    }

    private CursorType getAlternatesWidgetCursor(RecipeAlternativesWidgetAccessor alternatesWidget) {
        for (AbstractWidget alternativeButton : alternatesWidget.getAlternativeButtons()) {
            if (alternativeButton.isActive() && alternativeButton.isHovered()) {
                return CursorTypeUtil.canShift() ? CursorType.SHIFT : CursorType.POINTER;
            }
        }
        return CursorType.DEFAULT_FORCE;
    }

    private boolean isButtonHovered(RecipeBookWidgetAccessor recipeBook, RecipeBookResultsAccessor recipesArea) {
        StateSwitchingButton prevPageButton = recipesArea.getPrevPageButton();
        StateSwitchingButton nextPageButton = recipesArea.getNextPageButton();
        return (prevPageButton.isHovered() && prevPageButton.visible) || (nextPageButton.isHovered() && nextPageButton.visible) || recipeBook.getToggleCraftableButton().isHovered();
    }

    private CursorType getTabCursor(RecipeBookWidgetAccessor recipeBook) {
        for (RecipeBookTabButton tabButton : recipeBook.getTabButtons()) {
            if (tabButton.isHovered() && tabButton.isActive() && tabButton != recipeBook.getCurrentTab()) {
                return CursorType.POINTER;
            }
        }
        return CursorType.DEFAULT;
    }
}
