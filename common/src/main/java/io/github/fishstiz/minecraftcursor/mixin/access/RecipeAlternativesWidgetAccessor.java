package io.github.fishstiz.minecraftcursor.mixin.access;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(OverlayRecipeComponent.class)
public interface RecipeAlternativesWidgetAccessor {
    @Accessor("recipeButtons")
    List<? extends AbstractWidget> getAlternativeButtons();
}
