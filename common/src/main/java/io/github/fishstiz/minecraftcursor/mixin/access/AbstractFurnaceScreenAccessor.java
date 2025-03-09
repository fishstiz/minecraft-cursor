package io.github.fishstiz.minecraftcursor.mixin.access;

import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceScreen.class)
public interface AbstractFurnaceScreenAccessor extends HandledScreenAccessor<AbstractFurnaceMenu> {
    @Accessor("recipeBookComponent")
    AbstractFurnaceRecipeBookComponent getRecipeBook();
}
