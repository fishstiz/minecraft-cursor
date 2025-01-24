package io.github.fishstiz.minecraftcursor.mixin.client.access;

import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookScreen.class)
public interface RecipeBookScreenAccessor<T extends AbstractRecipeScreenHandler> extends HandledScreenAccessor<T> {
    @Accessor("recipeBook")
    RecipeBookWidget<T> getRecipeBook();
}
