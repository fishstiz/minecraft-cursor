package io.github.fishstiz.minecraftcursor.mixin.client;

import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.recipebook.AbstractFurnaceRecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceScreen.class)
public interface AbstractFurnaceScreenAccessor extends HandledScreenAccessor<AbstractFurnaceScreenHandler> {
    @Accessor("recipeBook")
    AbstractFurnaceRecipeBookScreen getRecipeBook();
}
