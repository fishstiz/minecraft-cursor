package io.github.fishstiz.minecraftcursor.registry.gui.recipebook;

import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;

import java.util.List;
import java.util.function.Supplier;

public class RecipeBookWidgetReflect {
    public Runnable reflect;
    public Supplier<Boolean> isOpen;
    public TextFieldWidget searchField;
    public ToggleButtonWidget toggleCraftableButton;
    public List<RecipeGroupButtonWidget> tabButtons;
    public RecipeGroupButtonWidget currentTab;
    public RecipeBookResultsReflect recipesArea = new RecipeBookResultsReflect();
}
