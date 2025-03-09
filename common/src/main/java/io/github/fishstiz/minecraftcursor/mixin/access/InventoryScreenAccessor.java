package io.github.fishstiz.minecraftcursor.mixin.access;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InventoryScreen.class)
public interface InventoryScreenAccessor extends HandledScreenAccessor<InventoryMenu> {
    @Accessor("recipeBookComponent")
    RecipeBookComponent getRecipeBook();
}
