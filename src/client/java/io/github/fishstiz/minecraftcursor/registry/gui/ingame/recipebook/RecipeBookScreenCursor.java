package io.github.fishstiz.minecraftcursor.registry.gui.ingame.recipebook;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.widget.ClickableWidget;

// knees weak
public class RecipeBookScreenCursor {
    private static RecipeBookScreenCursor instance;

    public RecipeBookWidgetReflect recipeBook = new RecipeBookWidgetReflect();

    private RecipeBookScreenCursor() {
    }

    public static void register(CursorTypeRegistry cursorRegistry) {
        cursorRegistry.register(RecipeBookScreen.class, RecipeBookScreenCursor.getInstance()::getCursorType);
    }

    public static RecipeBookScreenCursor getInstance() {
        if (instance == null) {
            instance = new RecipeBookScreenCursor();
        }
        return instance;
    }

    private CursorType getCursorType(Element element, double mouseX, double mouseY) {
        try {
            if (recipeBook.isOpen == null || !recipeBook.isOpen.get()) {
                return CursorType.DEFAULT;
            }
            RecipeBookResultsReflect recipes = recipeBook.recipesArea;
            recipeBook.reflect.run();
            recipes.reflect.run();

            if (recipes.alternatesWidget.isVisible.get()) {
                return recipes.alternatesWidget.alternativeButtons.stream().anyMatch(ClickableWidget::isHovered)
                        ? CursorType.POINTER
                        : CursorType.DEFAULT;
            }
            recipes.alternatesWidget.clear();

            boolean isButtonHovered = recipes.prevPageButton.isHovered() && recipes.prevPageButton.visible
                    || recipes.nextPageButton.isHovered() && recipes.nextPageButton.visible
                    || recipeBook.toggleCraftableButton.isHovered()
                    || recipes.hoveredResultButton != null;
            if (isButtonHovered) {
                return CursorType.POINTER;
            }

            if (recipeBook.searchField.isHovered()) {
                return CursorType.TEXT;
            }

            boolean isUnselectedTabHovered = recipeBook.tabButtons.stream()
                    .anyMatch(button -> button.isHovered() && button != recipeBook.currentTab);
            return isUnselectedTabHovered ? CursorType.POINTER : CursorType.DEFAULT;
        } catch (NullPointerException e) {
            MinecraftCursor.LOGGER.error("Error occurred while computing cursor type in RecipeBookScreen", e);
        }
        return CursorType.DEFAULT;
    }
}
