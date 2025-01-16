package io.github.fishstiz.minecraftcursor.mixin.client;

import io.github.fishstiz.minecraftcursor.registry.gui.recipebook.RecipeBookScreenCursor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RecipeBookResults.class)
public abstract class RecipeBookResultsMixin {
    @Unique
    private RecipeBookScreenCursor cursorHandler;
    @Shadow
    private @Nullable AnimatedResultButton hoveredResultButton;
    @Shadow
    private ToggleButtonWidget prevPageButton;
    @Shadow
    private ToggleButtonWidget nextPageButton;
    @Shadow
    @Final
    private RecipeAlternativesWidget alternatesWidget;
    @Shadow
    @Final
    private List<AnimatedResultButton> resultButtons;

    @Inject(method = "initialize", at = @At("TAIL"))
    public void init(MinecraftClient client, int parentLeft, int parentTop, CallbackInfo ci) {
        cursorHandler = RecipeBookScreenCursor.getInstance();
        cursorHandler.recipeBook.recipesArea.reflect = this::reflectProperties;
        reflectProperties();
    }

    @Unique
    public void reflectProperties() {
        cursorHandler.recipeBook.recipesArea.nextPageButton = this.nextPageButton;
        cursorHandler.recipeBook.recipesArea.prevPageButton = this.prevPageButton;
        cursorHandler.recipeBook.recipesArea.hoveredResultButton = this.hoveredResultButton;
        cursorHandler.recipeBook.recipesArea.resultButtons = this.resultButtons;
        cursorHandler.recipeBook.recipesArea.alternatesWidget.isVisible = this.alternatesWidget::isVisible;
    }
}
