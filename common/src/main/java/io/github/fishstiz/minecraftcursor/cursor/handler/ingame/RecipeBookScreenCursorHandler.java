package io.github.fishstiz.minecraftcursor.cursor.handler.ingame;

import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.cursorhandler.access.*;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.world.inventory.RecipeBookMenu;

public class RecipeBookScreenCursorHandler<T extends AbstractContainerScreen<? extends RecipeBookMenu<?, ?>>> implements CursorHandler<T> {
    public static final RecipeBookScreenCursorHandler<InventoryScreen> INVENTORY = new RecipeBookScreenCursorHandler<>();
    public static final RecipeBookScreenCursorHandler<CraftingScreen> CRAFTING = new RecipeBookScreenCursorHandler<>();
    public static final RecipeBookScreenCursorHandler<AbstractFurnaceScreen<?>> FURNACE = new RecipeBookScreenCursorHandler<>();

    private RecipeBookScreenCursorHandler() {
    }

    @Override
    public CursorType getCursorType(T recipeBookScreen, double mouseX, double mouseY) {
        RecipeBookWidgetAccessor recipeBook = getRecipeBook(recipeBookScreen);
        if (recipeBook == null || !recipeBook.invokeIsOpen()) return CursorType.DEFAULT;

        RecipeBookResultsAccessor recipesArea = (RecipeBookResultsAccessor) recipeBook.getRecipesArea();
        OverlayRecipeComponent alternatesWidget = recipesArea.getAlternatesWidget();

        if (alternatesWidget.isVisible()) {
            return getAlternatesWidgetCursor((RecipeAlternativesWidgetAccessor) alternatesWidget);
        }

        if (recipesArea.getHoveredResultButton() != null && CursorTypeUtil.canShift()) {
            return CursorType.SHIFT;
        } else if (isButtonHovered(recipeBook, recipesArea) || recipesArea.getHoveredResultButton() != null) {
            return CursorType.POINTER;
        } else if (recipeBook.getSearchField().isHovered()) {
            return CursorType.TEXT;
        }
        return getTabCursor(recipeBook);
    }

    private RecipeBookWidgetAccessor getRecipeBook(AbstractContainerScreen<?> screen) {
        return switch (screen) {
            case InventoryScreen inventory ->
                    (RecipeBookWidgetAccessor) ((InventoryScreenAccessor) inventory).getRecipeBook();
            case CraftingScreen crafting ->
                    (RecipeBookWidgetAccessor) ((CraftingScreenAccessor) crafting).getRecipeBook();
            case AbstractFurnaceScreen<?> furnace ->
                    (RecipeBookWidgetAccessor) ((AbstractFurnaceScreenAccessor) furnace).getRecipeBook();
            default -> null;
        };
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
        return (recipesArea.getPrevPageButton().isHovered() && recipesArea.getPrevPageButton().visible) ||
               (recipesArea.getNextPageButton().isHovered() && recipesArea.getNextPageButton().visible) ||
               recipeBook.getToggleCraftableButton().isHovered();
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
