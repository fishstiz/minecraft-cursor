package io.github.fishstiz.minecraftcursor.registry.gui.recipebook;

import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.widget.ToggleButtonWidget;

import java.util.List;

public class RecipeBookResultsReflect {
    public Runnable reflect;
    public ToggleButtonWidget prevPageButton;
    public ToggleButtonWidget nextPageButton;
    public AnimatedResultButton hoveredResultButton;
    public List<AnimatedResultButton> resultButtons;
    public RecipeAlternativesWidgetReflect alternatesWidget = new RecipeAlternativesWidgetReflect();
}
