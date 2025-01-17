package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.registry.utils.LookupUtils;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.*;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.util.List;

public class RecipeBookScreenCursor {
    public static final String RECIPE_BOOK_NAME = "field_54474";
    public static VarHandle recipeBook;

    // RecipeBookWidget
    public static final String IS_OPEN_NAME = "method_2605";
    public static final String IS_OPEN_DESC = "()Z";
    public static MethodHandle isOpen;
    public static final String SEARCH_FIELD_NAME = "field_3089";
    public static VarHandle searchField;
    public static final String TOGGLE_BTN_NAME = "field_3088";
    public static VarHandle toggleCraftableButton;
    public static final String TAB_BTNS_NAME = "field_3094";
    public static VarHandle tabButtons;
    public static final String CURRENT_TAB_NAME = "field_3098";
    public static VarHandle currentTab;
    public static final String RECIPES_AREA_NAME = "field_3086";
    public static VarHandle recipesArea; // RecipeBookResults

    // RecipeBookResults
    public static final String HOVERED_RESULT_BTN_NAME = "field_3129";
    public static VarHandle hoveredResultButton;
    public static final String PREV_PAGE_BTN_NAME = "field_3130";
    public static VarHandle prevPageButton;
    public static final String NEXT_PAGE_BTN_NAME = "field_3128";
    public static VarHandle nextPageButton;
    public static final String RESULT_BTNS_NAME = "field_3131";
    public static VarHandle resultButtons;
    public static final String ALTERNATES_WIDGET_NAME = "field_3132";
    public static VarHandle alternatesWidget; // RecipeAlternativesWidget

    // RecipeAlternativesWidget
    public static final String ALTERNATIVE_BTNS_NAME = "field_3106";
    public static VarHandle alternativeButtons;

    public static void register(CursorTypeRegistry cursorRegistry) {
        try {
            initScreenHandles();
            initWidgetHandles();
            initResultsHandles();
            initAlternativesHandles();
            cursorRegistry.register(RecipeBookScreen.class, RecipeBookScreenCursor::getCursorType);
        } catch (IllegalAccessException | NoSuchMethodException | NoSuchFieldException e) {
            MinecraftCursor.LOGGER.warn("Could not register cursor type for RecipeBookScreen");
        }
    }

    private static void initScreenHandles() throws NoSuchFieldException, IllegalAccessException {
        recipeBook = LookupUtils.getVarHandle(RecipeBookScreen.class, RECIPE_BOOK_NAME, RecipeBookWidget.class);
    }

    private static void initWidgetHandles() throws IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        Class<?> targetClass = RecipeBookWidget.class;
        isOpen = LookupUtils.getMethodHandle(targetClass, IS_OPEN_NAME, IS_OPEN_DESC, boolean.class);
        searchField = LookupUtils.getVarHandle(targetClass, SEARCH_FIELD_NAME, TextFieldWidget.class);
        toggleCraftableButton = LookupUtils.getVarHandle(targetClass, TOGGLE_BTN_NAME, ToggleButtonWidget.class);
        tabButtons = LookupUtils.getVarHandle(targetClass, TAB_BTNS_NAME, List.class);
        currentTab = LookupUtils.getVarHandle(targetClass, CURRENT_TAB_NAME, RecipeGroupButtonWidget.class);
        recipesArea = LookupUtils.getVarHandle(targetClass, RECIPES_AREA_NAME, RecipeBookResults.class);
    }

    private static void initResultsHandles() throws IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        Class<?> targetClass = RecipeBookResults.class;
        hoveredResultButton = LookupUtils.getVarHandle(targetClass, HOVERED_RESULT_BTN_NAME, AnimatedResultButton.class);
        prevPageButton = LookupUtils.getVarHandle(targetClass, PREV_PAGE_BTN_NAME, ToggleButtonWidget.class);
        nextPageButton = LookupUtils.getVarHandle(targetClass, NEXT_PAGE_BTN_NAME, ToggleButtonWidget.class);
        alternatesWidget = LookupUtils.getVarHandle(targetClass, ALTERNATES_WIDGET_NAME, RecipeAlternativesWidget.class);
        resultButtons = LookupUtils.getVarHandle(targetClass, RESULT_BTNS_NAME, List.class);
    }

    public static void initAlternativesHandles() throws IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        alternativeButtons = LookupUtils.getVarHandle(RecipeAlternativesWidget.class, ALTERNATIVE_BTNS_NAME, List.class);
    }

    @SuppressWarnings("unchecked")
    private static CursorType getCursorType(Element element, double mouseX, double mouseY) {
        try {
            RecipeBookWidget<?> recipeBook = (RecipeBookWidget<?>) RecipeBookScreenCursor.recipeBook.get(element);

            if (!((boolean) isOpen.invoke(recipeBook))) {
                return CursorType.DEFAULT;
            }

            RecipeBookResults recipeResults = (RecipeBookResults) RecipeBookScreenCursor.recipesArea.get(recipeBook);
            RecipeAlternativesWidget recipeAlternatives = (RecipeAlternativesWidget) alternatesWidget.get(recipeResults);

            if (recipeAlternatives.isVisible()) {
                return ((List<ClickableWidget>) alternativeButtons.get(recipeAlternatives)).stream().anyMatch(ClickableWidget::isHovered)
                        ? CursorType.POINTER
                        : CursorType.DEFAULT;
            }

            ToggleButtonWidget prevPageBtn = (ToggleButtonWidget) prevPageButton.get(recipeResults);
            ToggleButtonWidget nextPageBtn = (ToggleButtonWidget) nextPageButton.get(recipeResults);

            boolean isButtonHovered = prevPageBtn.isHovered() && prevPageBtn.visible
                    || nextPageBtn.isHovered() && nextPageBtn.visible
                    || ((ToggleButtonWidget) toggleCraftableButton.get(recipeBook)).isHovered()
                    || ((AnimatedResultButton) hoveredResultButton.get(recipeResults)) != null;

            if (isButtonHovered) {
                return CursorType.POINTER;
            }

            if (((TextFieldWidget) searchField.get(recipeBook)).isHovered()) {
                return CursorType.TEXT;
            }

            boolean isUnselectedTabHovered = ((List<RecipeGroupButtonWidget>) tabButtons.get(recipeBook))
                    .stream()
                    .anyMatch(btn -> btn.isHovered() && btn != currentTab.get(recipeBook));

            return isUnselectedTabHovered ? CursorType.POINTER : CursorType.DEFAULT;
        } catch (Throwable e) {
            MinecraftCursor.LOGGER.warn("Could not get cursor type for RecipeBookScreen");
        }
        return CursorType.DEFAULT;
    }
}
