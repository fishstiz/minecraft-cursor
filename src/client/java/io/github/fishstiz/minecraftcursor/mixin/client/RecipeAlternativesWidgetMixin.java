package io.github.fishstiz.minecraftcursor.mixin.client;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.registry.screen.RecipeBookScreenCursorRegistry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.recipebook.CurrentIndexProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RecipeAlternativesWidget.class)
public abstract class RecipeAlternativesWidgetMixin {
    @Shadow
    public abstract boolean isVisible();

    @Unique
    private RecipeBookScreenCursorRegistry cursorRegistry;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(CurrentIndexProvider currentIndexProvider, boolean furnace, CallbackInfo ci) {
        cursorRegistry = MinecraftCursorClient.getScreenCursorRegistry().recipeBookScreenCursorRegistry;
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!this.isVisible()) {
            return;
        }
    }
}
