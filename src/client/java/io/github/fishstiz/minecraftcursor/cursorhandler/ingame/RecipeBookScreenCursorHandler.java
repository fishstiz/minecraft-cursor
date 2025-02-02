package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.client.access.*;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.screen.AbstractRecipeScreenHandler;

public class RecipeBookScreenCursorHandler<T extends HandledScreen<? extends AbstractRecipeScreenHandler<?, ?>>> extends HandledScreenCursorHandler<AbstractRecipeScreenHandler<?, ?>, T> {
    public static final RecipeBookScreenCursorHandler<InventoryScreen> INVENTORY = new RecipeBookScreenCursorHandler<>();
    public static final RecipeBookScreenCursorHandler<CraftingScreen> CRAFTING = new RecipeBookScreenCursorHandler<>();
    public static final RecipeBookScreenCursorHandler<AbstractFurnaceScreen<?>> FURNACE = new RecipeBookScreenCursorHandler<>();

    private RecipeAlternativesWidget alternatesWidget;

    @Override
    public CursorType getCursorType(T recipeBookScreen, double mouseX, double mouseY) {
        CursorType cursorType = super.getCursorType(recipeBookScreen, mouseX, mouseY);
        if (cursorType != CursorType.DEFAULT && (alternatesWidget == null || !alternatesWidget.isVisible())) {
            return cursorType;
        }

        RecipeBookWidgetAccessor recipeBook = getRecipeBook(recipeBookScreen);
        if (recipeBook == null || !recipeBook.invokeIsOpen()) return CursorType.DEFAULT;

        RecipeBookResultsAccessor recipesArea = (RecipeBookResultsAccessor) recipeBook.getRecipesArea();
        alternatesWidget = recipesArea.getAlternatesWidget();

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

    private RecipeBookWidgetAccessor getRecipeBook(HandledScreen<?> screen) {
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
        return alternatesWidget.getAlternativeButtons().stream().anyMatch(ClickableWidget::isHovered)
                ? (CursorTypeUtil.canShift() ? CursorType.SHIFT : CursorType.POINTER)
                : CursorType.DEFAULT_FORCE;
    }

    private boolean isButtonHovered(RecipeBookWidgetAccessor recipeBook, RecipeBookResultsAccessor recipesArea) {
        return (recipesArea.getPrevPageButton().isHovered() && recipesArea.getPrevPageButton().visible) ||
                (recipesArea.getNextPageButton().isHovered() && recipesArea.getNextPageButton().visible) ||
                recipeBook.getToggleCraftableButton().isHovered();
    }

    private CursorType getTabCursor(RecipeBookWidgetAccessor recipeBook) {
        return recipeBook.getTabButtons().stream().anyMatch(btn -> btn.isHovered() && btn != recipeBook.getCurrentTab())
                ? CursorType.POINTER
                : CursorType.DEFAULT;
    }
}
