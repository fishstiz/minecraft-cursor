package io.github.fishstiz.minecraftcursor.registry.screen;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.ScreenCursorRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;

import java.lang.reflect.Field;
import java.util.List;

public class InventoryScreenCursorRegistry {
    private Field recipeBookField;
    private Field searchFieldField;
    private Field toggleCraftableButtonField;
    private Field tabButtonsField;
    private Field currentTabField;

    public InventoryScreenCursorRegistry(ScreenCursorRegistry screenCursorRegistry) {
        try {
            recipeBookField = RecipeBookScreen.class.getDeclaredField("recipeBook");
            recipeBookField.setAccessible(true);
            searchFieldField = RecipeBookWidget.class.getDeclaredField("searchField");
            searchFieldField.setAccessible(true);
            toggleCraftableButtonField = RecipeBookWidget.class.getDeclaredField("toggleCraftableButton");
            toggleCraftableButtonField.setAccessible(true);
            tabButtonsField = RecipeBookWidget.class.getDeclaredField("tabButtons");
            tabButtonsField.setAccessible(true);
            currentTabField = RecipeBookWidget.class.getDeclaredField("currentTab");
            currentTabField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            MinecraftCursor.LOGGER.error("No such fields exist on InventoryScreen", e);
        }

        screenCursorRegistry.register(InventoryScreen.class, this::getCursorType);
    }

    @SuppressWarnings("unchecked")
    private CursorType getCursorType(Element element, double mouseX, double mouseY, float delta) {
        try {
            InventoryScreen inventoryScreen = (InventoryScreen) element;
            RecipeBookWidget<?> recipeBook = (RecipeBookWidget<?>) recipeBookField.get(inventoryScreen);

            if (((TextFieldWidget) searchFieldField.get(recipeBook)).isHovered()) {
                return CursorType.TEXT;
            }

            if (((ToggleButtonWidget) toggleCraftableButtonField.get(recipeBook)).isHovered()) {
                return CursorType.POINTER;
            }

            List<RecipeGroupButtonWidget> tabButtons = (List<RecipeGroupButtonWidget>) tabButtonsField.get(recipeBook);
            RecipeGroupButtonWidget currentTab = (RecipeGroupButtonWidget) currentTabField.get(recipeBook);
            for (RecipeGroupButtonWidget tabButton : tabButtons) {
                if (tabButton.isHovered() && tabButton != currentTab) {
                    return CursorType.POINTER;
                }
            }
        } catch (IllegalAccessException e) {
            MinecraftCursor.LOGGER.error("Could not check CursorType on InventoryScreen", e);
        }

        return CursorType.DEFAULT;
    }
}
