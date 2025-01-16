package io.github.fishstiz.minecraftcursor.registry.gui.recipebook;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class RecipeBookScreenCursor {
    private static RecipeBookScreenCursor instance;

    public Runnable reflectRecipeBookWidget;
    public Supplier<Boolean> isOpen;
    public @Nullable TextFieldWidget searchField;
    public @Nullable ToggleButtonWidget toggleCraftableButton;
    public List<RecipeGroupButtonWidget> tabButtons;
    public @Nullable RecipeGroupButtonWidget currentTab;

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
        if (isOpen == null || !isOpen.get()) {
            return CursorType.DEFAULT;
        }
        reflectRecipeBookWidget.run();

        if (searchField != null && searchField.isHovered()) {
            return CursorType.TEXT;
        }
        if (toggleCraftableButton != null && toggleCraftableButton.isHovered()) {
            return CursorType.POINTER;
        }
        boolean isUnselectedTabHovered = tabButtons.stream()
                .anyMatch(button -> button.isHovered() && button != currentTab);
        if (isUnselectedTabHovered) {
            return CursorType.POINTER;
        }
        return CursorType.DEFAULT;
    }
}
