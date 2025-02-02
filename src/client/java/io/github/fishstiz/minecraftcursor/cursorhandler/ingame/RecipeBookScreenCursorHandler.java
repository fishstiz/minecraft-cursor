package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.client.access.RecipeAlternativesWidgetAccessor;
import io.github.fishstiz.minecraftcursor.mixin.client.access.RecipeBookResultsAccessor;
import io.github.fishstiz.minecraftcursor.mixin.client.access.RecipeBookScreenAccessor;
import io.github.fishstiz.minecraftcursor.mixin.client.access.RecipeBookWidgetAccessor;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.*;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.screen.AbstractRecipeScreenHandler;

public class RecipeBookScreenCursorHandler extends HandledScreenCursorHandler<AbstractRecipeScreenHandler, RecipeBookScreen<? extends AbstractRecipeScreenHandler>> {
    private RecipeAlternativesWidgetAccessor alternatesWidget;

    @Override
    public CursorType getCursorType(RecipeBookScreen<? extends AbstractRecipeScreenHandler> recipeBookScreen, double mouseX, double mouseY) {
        CursorType cursorType = super.getCursorType(recipeBookScreen, mouseX, mouseY);
        if (cursorType != CursorType.DEFAULT && (alternatesWidget == null || !(((RecipeAlternativesWidget) alternatesWidget).isVisible()))) {
            return cursorType;
        }

        RecipeBookWidgetAccessor recipeBook = (RecipeBookWidgetAccessor) ((RecipeBookScreenAccessor<?>) recipeBookScreen).getRecipeBook();
        if (!recipeBook.invokeIsOpen()) return CursorType.DEFAULT;

        RecipeBookResultsAccessor recipesArea = (RecipeBookResultsAccessor) recipeBook.getRecipesArea();
        alternatesWidget = (RecipeAlternativesWidgetAccessor) recipesArea.getAlternatesWidget();

        if (((RecipeAlternativesWidget) alternatesWidget).isVisible()) {
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
        if (alternatesWidget.getAlternativeButtons().stream().anyMatch(ClickableWidget::isHovered)) {
            return CursorTypeUtil.canShift() ? CursorType.SHIFT : CursorType.POINTER;
        }
        return CursorType.DEFAULT_FORCE;
    }

    private boolean isButtonHovered(RecipeBookWidgetAccessor recipeBook, RecipeBookResultsAccessor recipesArea) {
        ToggleButtonWidget prevPageButton = recipesArea.getPrevPageButton();
        ToggleButtonWidget nextPageButton = recipesArea.getNextPageButton();
        return (prevPageButton.isHovered() && prevPageButton.visible) || (nextPageButton.isHovered() && nextPageButton.visible) || recipeBook.getToggleCraftableButton().isHovered();
    }

    private CursorType getTabCursor(RecipeBookWidgetAccessor recipeBook) {
        boolean isUnselectedTabHovered = recipeBook.getTabButtons().stream().anyMatch(btn -> btn.isHovered() && btn != recipeBook.getCurrentTab());
        return isUnselectedTabHovered ? CursorType.POINTER : CursorType.DEFAULT;
    }
}
