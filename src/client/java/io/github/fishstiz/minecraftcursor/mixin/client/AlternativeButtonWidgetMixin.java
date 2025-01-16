package io.github.fishstiz.minecraftcursor.mixin.client;

import io.github.fishstiz.minecraftcursor.registry.gui.recipebook.RecipeAlternativesWidgetReflect;
import io.github.fishstiz.minecraftcursor.registry.gui.recipebook.RecipeBookScreenCursor;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(targets = "net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget$AlternativeButtonWidget")
public abstract class AlternativeButtonWidgetMixin extends ClickableWidget {
    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(RecipeAlternativesWidget recipeAlternativesWidget, int x, int y, NetworkRecipeId recipeId, boolean craftable, List<?> inputSlots, CallbackInfo ci) {
        RecipeAlternativesWidgetReflect alternatesWidget = RecipeBookScreenCursor.getInstance().recipeBook.recipesArea.alternatesWidget;
        reflectButton(alternatesWidget);
    }

    @Unique
    public void reflectButton(RecipeAlternativesWidgetReflect alternatesWidget) {
        alternatesWidget.alternativeButtons.add(this);
    }

    public AlternativeButtonWidgetMixin(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }
}
