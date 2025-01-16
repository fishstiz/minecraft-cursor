package io.github.fishstiz.minecraftcursor.mixin.client;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.registry.screen.RecipeBookScreenCursorRegistry;
import net.minecraft.client.gui.DrawContext;
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
    @Unique
    private RecipeBookScreenCursorRegistry cursorRegistry;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(RecipeAlternativesWidget recipeAlternativesWidget, int x, int y, NetworkRecipeId recipeId, boolean craftable, List<?> inputSlots, CallbackInfo ci) {
        cursorRegistry = MinecraftCursorClient.getScreenCursorRegistry().recipeBookScreenCursorRegistry;
    }

    @Inject(method = "renderWidget", at = @At("TAIL"))
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
//        if (this.isMouseOver(mouseX, mouseY)) {
//            cursorRegistry.setRecipeAlternativeHovered(true);
//        }
    }

    public AlternativeButtonWidgetMixin(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }
}
