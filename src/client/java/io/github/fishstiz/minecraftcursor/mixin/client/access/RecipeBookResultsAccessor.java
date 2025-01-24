package io.github.fishstiz.minecraftcursor.mixin.client.access;

import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookResults.class)
public interface RecipeBookResultsAccessor {
    @Accessor("hoveredResultButton")
    AnimatedResultButton getHoveredResultButton();

    @Accessor("prevPageButton")
    ToggleButtonWidget getPrevPageButton();

    @Accessor("nextPageButton")
    ToggleButtonWidget getNextPageButton();

    @Accessor("alternatesWidget")
    RecipeAlternativesWidget getAlternatesWidget();
}
