package io.github.fishstiz.minecraftcursor.mixin.access;

import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.world.inventory.CraftingMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingScreen.class)
public interface CraftingScreenAccessor extends HandledScreenAccessor<CraftingMenu> {
    @Accessor("recipeBookComponent")
    RecipeBookComponent getRecipeBook();
}
