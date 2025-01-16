package io.github.fishstiz.minecraftcursor.mixin.client;

import io.github.fishstiz.minecraftcursor.registry.gui.ingame.recipebook.RecipeBookScreenCursor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
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

@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookWidgetMixin {
    @Unique
    private RecipeBookScreenCursor cursorHandler;
    @Shadow
    public abstract boolean isOpen();
    @Shadow
    private @Nullable TextFieldWidget searchField;
    @Shadow
    protected ToggleButtonWidget toggleCraftableButton;
    @Shadow
    @Final
    private List<RecipeGroupButtonWidget> tabButtons;
    @Shadow
    private @Nullable RecipeGroupButtonWidget currentTab;

    @Inject(method = "initialize", at = @At("TAIL"))
    public void init(int parentWidth, int parentHeight, MinecraftClient client, boolean narrow, CallbackInfo ci) {
        cursorHandler = RecipeBookScreenCursor.getInstance();
        cursorHandler.recipeBook.reflect = this::reflectProperties;
        reflectProperties();
    }

    @Unique
    private void reflectProperties() {
        cursorHandler.recipeBook.isOpen = this::isOpen;
        cursorHandler.recipeBook.searchField = this.searchField;
        cursorHandler.recipeBook.toggleCraftableButton = this.toggleCraftableButton;
        cursorHandler.recipeBook.tabButtons = this.tabButtons;
        cursorHandler.recipeBook.currentTab = this.currentTab;
    }
}
