package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.cursorhandler.access.RecipeAlternativesWidgetAccessor;
import io.github.fishstiz.minecraftcursor.mixin.cursorhandler.access.RecipeBookResultsAccessor;
import io.github.fishstiz.minecraftcursor.mixin.cursorhandler.access.RecipeBookWidgetAccessor;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.RecipeBookMenu;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractRecipeBookScreen.class)
public abstract class AbstractRecipeBookScreenMixin extends AbstractContainerScreenMixin<RecipeBookMenu> {
    @Shadow
    @Final
    private RecipeBookComponent<?> recipeBookComponent;

    protected AbstractRecipeBookScreenMixin(Component title) {
        super(title);
    }

    @Override
    public @NotNull CursorType minecraft_cursor$getCursorType(double mouseX, double mouseY) {
        if (this.recipeBookComponent.isVisible()) {
            RecipeBookWidgetAccessor recipeBook = (RecipeBookWidgetAccessor) this.recipeBookComponent;
            RecipeBookResultsAccessor recipesArea = (RecipeBookResultsAccessor) recipeBook.getRecipesArea();
            RecipeAlternativesWidgetAccessor alternatesWidget = (RecipeAlternativesWidgetAccessor) recipesArea.getAlternatesWidget();

            if (((OverlayRecipeComponent) alternatesWidget).isVisible()) {
                return minecraft_cursor$getAlternatesWidgetCursor(alternatesWidget);
            }

            boolean isResultHovered = recipesArea.getHoveredResultButton() != null;
            if (isResultHovered && CursorTypeUtil.canShift()) {
                return CursorType.SHIFT;
            } else if (this.minecraft_cursor$isButtonHovered(recipeBook, recipesArea) || isResultHovered) {
                return CursorType.POINTER;
            } else if (recipeBook.getSearchField().isHovered()) {
                return CursorType.TEXT;
            }

            CursorType tabCursorType = this.minecraft_cursor$getTabCursor(recipeBook);
            if (!tabCursorType.isDefault()) {
                return tabCursorType;
            }
        }

        return super.minecraft_cursor$getCursorType(mouseX, mouseY);
    }

    @Unique
    private CursorType minecraft_cursor$getAlternatesWidgetCursor(RecipeAlternativesWidgetAccessor alternatesWidget) {
        for (AbstractWidget alternativeButton : alternatesWidget.getAlternativeButtons()) {
            if (alternativeButton.isActive() && alternativeButton.isHovered()) {
                return CursorTypeUtil.canShift() ? CursorType.SHIFT : CursorType.POINTER;
            }
        }
        return CursorType.DEFAULT_FORCE;
    }

    @Unique
    private boolean minecraft_cursor$isButtonHovered(RecipeBookWidgetAccessor recipeBook, RecipeBookResultsAccessor recipesArea) {
        StateSwitchingButton prevPageButton = recipesArea.getPrevPageButton();
        StateSwitchingButton nextPageButton = recipesArea.getNextPageButton();
        StateSwitchingButton toggleCraftableButton = recipeBook.getToggleCraftableButton();

        return (toggleCraftableButton.visible && toggleCraftableButton.isHovered()) ||
               (prevPageButton.visible && prevPageButton.isHovered()) ||
               (nextPageButton.visible && nextPageButton.isHovered());
    }

    @Unique
    private CursorType minecraft_cursor$getTabCursor(RecipeBookWidgetAccessor recipeBook) {
        for (RecipeBookTabButton tabButton : recipeBook.getTabButtons()) {
            if (tabButton.isHovered() && tabButton.isActive() && tabButton != recipeBook.getCurrentTab()) {
                return CursorType.POINTER;
            }
        }
        return CursorType.DEFAULT;
    }
}
