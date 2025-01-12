package io.github.fishstiz.minecraftcursor.mixin.client;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.ScreenCursorRegistry;
import io.github.fishstiz.minecraftcursor.registry.WidgetCursorRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractParentElement implements Drawable {
    @Unique
    private CursorManager cursorManager;
    @Unique
    private ScreenCursorRegistry screenCursorRegistry;
    @Unique
    private WidgetCursorRegistry widgetCursorRegistry;

    @Shadow
    @Nullable
    protected MinecraftClient client;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        cursorManager = MinecraftCursorClient.getCursorManager();
        screenCursorRegistry = MinecraftCursorClient.getScreenCursorRegistry();
        widgetCursorRegistry = MinecraftCursorClient.getWidgetCursorRegistry();
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.client == null) {
            return;
        }

        if (this.client.currentScreen != (Object) this) {
            return;
        }

        CursorType screenCursorType = screenCursorRegistry.getCursorType(this, mouseX, mouseY, delta);

        CursorType widgetCursorType = CursorType.DEFAULT;
        Optional<? extends Element> hoveredElementOpt = this.hoveredElement(mouseX, mouseY);
        if (hoveredElementOpt.isPresent()) {
            Element hoveredElement = hoveredElementOpt.get();
            widgetCursorType = widgetCursorRegistry.getCursorType(hoveredElement, mouseX, mouseY, delta);
        }

        cursorManager.setCurrentCursor(screenCursorType != CursorType.DEFAULT ? screenCursorType : widgetCursorType);
    }
}
