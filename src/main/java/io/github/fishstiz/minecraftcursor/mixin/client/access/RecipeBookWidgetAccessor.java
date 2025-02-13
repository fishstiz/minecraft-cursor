package io.github.fishstiz.minecraftcursor.mixin.client.access;

import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(RecipeBookWidget.class)
public interface RecipeBookWidgetAccessor {
    @Invoker("isOpen")
    boolean invokeIsOpen();

    @Accessor("searchField")
    TextFieldWidget getSearchField();

    @Accessor("toggleCraftableButton")
    ToggleButtonWidget getToggleCraftableButton();

    @Accessor("tabButtons")
    List<RecipeGroupButtonWidget> getTabButtons();

    @Accessor("currentTab")
    RecipeGroupButtonWidget getCurrentTab();

    @Accessor("recipesArea")
    RecipeBookResults getRecipesArea();
}
