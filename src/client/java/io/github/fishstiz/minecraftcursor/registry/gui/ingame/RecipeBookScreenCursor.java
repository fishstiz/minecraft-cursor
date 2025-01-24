package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.client.RecipeAlternativesWidgetAccessor;
import io.github.fishstiz.minecraftcursor.mixin.client.RecipeBookResultsAccessor;
import io.github.fishstiz.minecraftcursor.mixin.client.RecipeBookWidgetAccessor;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.registry.utils.CursorTypeUtils;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.*;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.screen.AbstractRecipeScreenHandler;

public class RecipeBookScreenCursor extends HandledScreenCursor<AbstractRecipeScreenHandler> {
    public static void register(CursorTypeRegistry cursorRegistry) {
        cursorRegistry.register(RecipeBookScreen.class, new RecipeBookScreenCursor()::getCursorType);
    }

    @Override
    public CursorType getCursorType(Element element, double mouseX, double mouseY) {
        CursorType cursorType = super.getCursorType(element, mouseX, mouseY);
        if (cursorType != CursorType.DEFAULT) return cursorType;

        RecipeBookWidgetAccessor recipeBook = (RecipeBookWidgetAccessor) element;
        if (!recipeBook.isOpen()) return CursorType.DEFAULT;

        RecipeBookResultsAccessor recipesArea = (RecipeBookResultsAccessor) recipeBook.getRecipesArea();
        RecipeAlternativesWidgetAccessor alternatesWidget = (RecipeAlternativesWidgetAccessor) recipesArea.getAlternatesWidget();

        if (((RecipeAlternativesWidget) alternatesWidget).isVisible()) {
            return getAlternatesWidgetCursor(alternatesWidget);
        }

        boolean isResultHovered = recipesArea.getHoveredResultButton() != null;
        if (isResultHovered && CursorTypeUtils.canShift()) {
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
            return CursorTypeUtils.canShift() ? CursorType.SHIFT : CursorType.POINTER;
        }
        return CursorType.DEFAULT;
    }

    private boolean isButtonHovered(RecipeBookWidgetAccessor recipeBook, RecipeBookResultsAccessor recipesArea) {
        ToggleButtonWidget prevPageButton = recipesArea.getPrevPageButton();
        ToggleButtonWidget nextPageButton = recipesArea.getNextPageButton();
        return (prevPageButton.isHovered() && prevPageButton.visible) ||
                (nextPageButton.isHovered() && nextPageButton.visible) ||
                recipeBook.getToggleCraftableButton().isHovered();
    }

    private CursorType getTabCursor(RecipeBookWidgetAccessor recipeBook) {
        boolean isUnselectedTabHovered = recipeBook.getTabButtons().stream()
                .anyMatch(btn -> btn.isHovered() && btn != recipeBook.getCurrentTab());
        return isUnselectedTabHovered ? CursorType.POINTER : CursorType.DEFAULT;
    }
}
