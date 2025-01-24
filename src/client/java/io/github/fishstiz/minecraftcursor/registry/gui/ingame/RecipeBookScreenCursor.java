package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.client.*;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.registry.utils.CursorTypeUtils;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.*;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.screen.AbstractRecipeScreenHandler;

public class RecipeBookScreenCursor extends HandledScreenCursor<AbstractRecipeScreenHandler<?, ?>> {
    public static void register(CursorTypeRegistry cursorRegistry) {
        RecipeBookScreenCursor recipeBookScreenCursor = new RecipeBookScreenCursor();
        cursorRegistry.register(InventoryScreen.class, recipeBookScreenCursor::getCursorType);
        cursorRegistry.register(CraftingScreen.class, recipeBookScreenCursor::getCursorType);
        cursorRegistry.register(AbstractFurnaceScreen.class, recipeBookScreenCursor::getCursorType);
    }

    @Override
    public CursorType getCursorType(Element element, double mouseX, double mouseY) {
        CursorType cursorType = super.getCursorType(element, mouseX, mouseY);
        if (cursorType != CursorType.DEFAULT) return cursorType;

        RecipeBookWidgetAccessor recipeBook;
        switch (element) {
            case InventoryScreen inventoryScreen ->
                    recipeBook = (RecipeBookWidgetAccessor) ((InventoryScreenAccessor) inventoryScreen).getRecipeBook();
            case CraftingScreen craftingScreen ->
                    recipeBook = (RecipeBookWidgetAccessor) ((CraftingScreenAccessor) craftingScreen).getRecipeBook();
            case AbstractFurnaceScreen<?> abstractFurnaceScreen ->
                    recipeBook = (RecipeBookWidgetAccessor) ((AbstractFurnaceScreenAccessor) abstractFurnaceScreen).getRecipeBook();
            case null, default -> {
                return CursorType.DEFAULT;
            }
        }

        if (!recipeBook.invokeIsOpen()) return CursorType.DEFAULT;

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
