package io.github.fishstiz.minecraftcursor.mixin.client;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.registry.screen.RecipeBookScreenCursorRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
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
    private RecipeBookScreenCursorRegistry cursorRegistry;

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
        cursorRegistry = MinecraftCursorClient.getScreenCursorRegistry().recipeBookScreenCursorRegistry;
    }

    @Inject(method = "draw", at = @At("TAIL"))
    public void draw(DrawContext context, int x, int y, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        cursorRegistry.setRecipeAlternativesVisible(alternatesWidget.isVisible());
        cursorRegistry.setRecipeResultHovered(hoveredResultButton != null);
        cursorRegistry.setPageButtonHovered(prevPageButton.isMouseOver(mouseX, mouseY)
                || nextPageButton.isMouseOver(mouseX, mouseY));
    }
}
