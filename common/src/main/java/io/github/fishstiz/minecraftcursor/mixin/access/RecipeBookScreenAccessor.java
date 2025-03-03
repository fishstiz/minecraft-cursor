package io.github.fishstiz.minecraftcursor.mixin.access;

import io.github.fishstiz.minecraftcursor.mixin.access.HandledScreenAccessor;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.world.inventory.RecipeBookMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractRecipeBookScreen.class)
public interface RecipeBookScreenAccessor<T extends RecipeBookMenu> extends HandledScreenAccessor<T> {
    @Accessor("recipeBookComponent")
    RecipeBookComponent<T> getRecipeBook();
}
