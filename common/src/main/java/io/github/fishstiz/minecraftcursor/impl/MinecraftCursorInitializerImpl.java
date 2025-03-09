package io.github.fishstiz.minecraftcursor.impl;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.CursorTypeRegistrar;
import io.github.fishstiz.minecraftcursor.api.ElementRegistrar;
import io.github.fishstiz.minecraftcursor.api.MinecraftCursorInitializer;
import io.github.fishstiz.minecraftcursor.cursorhandler.ingame.*;
import io.github.fishstiz.minecraftcursor.cursorhandler.multiplayer.MultiplayerServerListWidgetCursorHandler;
import io.github.fishstiz.minecraftcursor.cursorhandler.world.WorldListWidgetCursorHandler;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;

public class MinecraftCursorInitializerImpl implements MinecraftCursorInitializer {
    @Override
    public void init(CursorTypeRegistrar cursorRegistrar, ElementRegistrar elementRegistrar) {
        cursorRegistrar.register(
                CursorType.DEFAULT,
                CursorType.POINTER,
                CursorType.GRABBING,
                CursorType.TEXT,
                CursorType.SHIFT,
                CursorType.BUSY,
                CursorType.CROSSHAIR,
                CursorType.RESIZE_EW,
                CursorType.RESIZE_NS,
                CursorType.RESIZE_NWSE,
                CursorType.RESIZE_NESW,
                CursorType.NOT_ALLOWED
        );

        elementRegistrar.register(GenericMessageScreen.class, MinecraftCursorInitializerImpl::elementToBusy);
        elementRegistrar.register(ReceivingLevelScreen.class, MinecraftCursorInitializerImpl::elementToBusy);
        elementRegistrar.register(ProgressScreen.class, MinecraftCursorInitializerImpl::elementToBusy);
        elementRegistrar.register(LevelLoadingScreen.class, MinecraftCursorInitializerImpl::elementToBusy);
        elementRegistrar.register(AbstractButton.class, MinecraftCursorInitializerImpl::clickableWidgetCursor);
        elementRegistrar.register(TabButton.class, MinecraftCursorInitializerImpl::tabButtonWidgetCursor);
        elementRegistrar.register(AbstractSliderButton.class, MinecraftCursorInitializerImpl::sliderWidgetCursor);
        elementRegistrar.register(EditBox.class, MinecraftCursorInitializerImpl::textFieldWidgetCursor);
        elementRegistrar.register(new WorldListWidgetCursorHandler());
        elementRegistrar.register(new HandledScreenCursorHandler<>());
        elementRegistrar.register(new MultiplayerServerListWidgetCursorHandler());
        elementRegistrar.register(RecipeBookScreenCursorHandler.INVENTORY);
        elementRegistrar.register(RecipeBookScreenCursorHandler.CRAFTING);
        elementRegistrar.register(RecipeBookScreenCursorHandler.FURNACE);
        elementRegistrar.register(new CreativeInventoryScreenCursorHandler());
        elementRegistrar.register(new BookEditScreenCursorHandler());
        elementRegistrar.register(new EnchantmentScreenCursorHandler());
        elementRegistrar.register(new StonecutterScreenCursorHandler());
        elementRegistrar.register(new LoomScreenCursorHandler());
        elementRegistrar.register(new CrafterScreenCursorHandler());
        elementRegistrar.register(new AdvancementsScreenCursorHandler());
    }

    private static <T extends GuiEventListener> CursorType elementToBusy(T ignoreElement, double ignoreMouseX, double ignoreMouseY) {
        return CursorType.BUSY;
    }

    private static <T extends AbstractWidget> CursorType clickableWidgetCursor(T clickable, double mouseX, double mouseY) {
        return clickable.active && clickable.visible && clickable.isHovered() ? CursorType.POINTER : CursorType.DEFAULT;
    }

    private static <T extends TabButton> CursorType tabButtonWidgetCursor(T tabButton, double mouseX, double mouseY) {
        return tabButton.active && tabButton.visible && tabButton.isHovered() && !tabButton.isSelected() ?
                CursorType.POINTER : CursorType.DEFAULT;
    }

    private static <T extends AbstractSliderButton> CursorType sliderWidgetCursor(T slider, double mouseX, double mouseY) {
        if (slider.isFocused() && (CursorTypeUtil.isLeftClickHeld() || CursorTypeUtil.isGrabbing())) {
            return CursorType.GRABBING;
        }
        return slider.active && slider.visible ? CursorType.POINTER : CursorType.DEFAULT;
    }

    private static <T extends EditBox> CursorType textFieldWidgetCursor(T textField, double mouseX, double mouseY) {
        return textField.visible && textField.isHovered() ? CursorType.TEXT : CursorType.DEFAULT;
    }
}
