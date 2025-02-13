package io.github.fishstiz.minecraftcursor.mixin.client.access;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InventoryScreen.class)
public interface InventoryScreenAccessor extends HandledScreenAccessor<PlayerScreenHandler> {
    @Accessor("recipeBook")
    RecipeBookWidget getRecipeBook();
}
