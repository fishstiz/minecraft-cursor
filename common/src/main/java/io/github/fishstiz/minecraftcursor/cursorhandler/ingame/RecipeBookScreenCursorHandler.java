package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.access.RecipeAlternativesWidgetAccessor;
import io.github.fishstiz.minecraftcursor.mixin.access.RecipeBookResultsAccessor;
import io.github.fishstiz.minecraftcursor.mixin.access.RecipeBookScreenAccessor;
import io.github.fishstiz.minecraftcursor.mixin.access.RecipeBookWidgetAccessor;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.world.inventory.RecipeBookMenu;

public class RecipeBookScreenCursorHandler extends HandledScreenCursorHandler<RecipeBookMenu, AbstractRecipeBookScreen<? extends RecipeBookMenu>> {
    private RecipeAlternativesWidgetAccessor alternatesWidget;

    @Override
    public CursorType getCursorType(AbstractRecipeBookScreen<? extends RecipeBookMenu> recipeBookScreen, double mouseX, double mouseY) {
        CursorType cursorType = super.getCursorType(recipeBookScreen, mouseX, mouseY);
        if (cursorType != CursorType.DEFAULT && (alternatesWidget == null || !(((OverlayRecipeComponent) alternatesWidget).isVisible()))) {
            return cursorType;
        }

        RecipeBookWidgetAccessor recipeBook = (RecipeBookWidgetAccessor) ((RecipeBookScreenAccessor<?>) recipeBookScreen).getRecipeBook();
        if (!recipeBook.invokeIsOpen()) return CursorType.DEFAULT;

        RecipeBookResultsAccessor recipesArea = (RecipeBookResultsAccessor) recipeBook.getRecipesArea();
        alternatesWidget = (RecipeAlternativesWidgetAccessor) recipesArea.getAlternatesWidget();

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
        if (alternatesWidget.getAlternativeButtons().stream().anyMatch(AbstractWidget::isHovered)) {
            return CursorTypeUtil.canShift() ? CursorType.SHIFT : CursorType.POINTER;
        }
        return CursorType.DEFAULT_FORCE;
    }

    private boolean isButtonHovered(RecipeBookWidgetAccessor recipeBook, RecipeBookResultsAccessor recipesArea) {
        StateSwitchingButton prevPageButton = recipesArea.getPrevPageButton();
        StateSwitchingButton nextPageButton = recipesArea.getNextPageButton();
        return (prevPageButton.isHovered() && prevPageButton.visible) || (nextPageButton.isHovered() && nextPageButton.visible) || recipeBook.getToggleCraftableButton().isHovered();
    }

    private CursorType getTabCursor(RecipeBookWidgetAccessor recipeBook) {
        boolean isUnselectedTabHovered = recipeBook.getTabButtons().stream().anyMatch(btn -> btn.isHovered() && btn != recipeBook.getCurrentTab());
        return isUnselectedTabHovered ? CursorType.POINTER : CursorType.DEFAULT;
    }
}
