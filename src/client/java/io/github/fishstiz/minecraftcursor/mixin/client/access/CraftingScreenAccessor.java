package io.github.fishstiz.minecraftcursor.mixin.client.access;

import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.screen.CraftingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingScreen.class)
public interface CraftingScreenAccessor extends HandledScreenAccessor<CraftingScreenHandler> {
    @Accessor("recipeBook")
    RecipeBookWidget getRecipeBook();
}
