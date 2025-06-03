package io.github.fishstiz.minecraftcursor.impl;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.CursorTypeRegistrar;
import io.github.fishstiz.minecraftcursor.api.ElementRegistrar;
import io.github.fishstiz.minecraftcursor.api.MinecraftCursorInitializer;
import io.github.fishstiz.minecraftcursor.cursor.handler.ingame.*;
import io.github.fishstiz.minecraftcursor.cursor.handler.multiplayer.MultiplayerServerListWidgetCursorHandler;
import io.github.fishstiz.minecraftcursor.cursor.handler.world.WorldListWidgetCursorHandler;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;

public final class MinecraftCursorInitializerImpl implements MinecraftCursorInitializer {
    @Override
    public void init(CursorTypeRegistrar cursorRegistrar, ElementRegistrar elementRegistrar) {
        cursorRegistrar.register(
                CursorType.DEFAULT,
                CursorType.POINTER,
                CursorType.GRABBING,
                CursorType.TEXT,
                CursorType.SHIFT,
                CursorType.BUSY,
                CursorType.NOT_ALLOWED,
                CursorType.CROSSHAIR,
                CursorType.RESIZE_EW,
                CursorType.RESIZE_NS,
                CursorType.RESIZE_NWSE,
                CursorType.RESIZE_NESW
        );

        elementRegistrar.register(ReceivingLevelScreen.class, this::elementToBusy);
        elementRegistrar.register(ProgressScreen.class, this::elementToBusy);
        elementRegistrar.register(LevelLoadingScreen.class, this::elementToBusy);
        elementRegistrar.register(AbstractButton.class, this::buttonWidgetCursor);
        elementRegistrar.register(TabButton.class, this::tabButtonWidgetCursor);
        elementRegistrar.register(AbstractSliderButton.class, this::sliderWidgetCursor);
        elementRegistrar.register(EditBox.class, this::textFieldWidgetCursor);
        elementRegistrar.register(MultiLineEditBox.class, this::textFieldWidgetCursor);
        elementRegistrar.register(new WorldListWidgetCursorHandler());
        elementRegistrar.register(new MultiplayerServerListWidgetCursorHandler());
        elementRegistrar.register(new RecipeBookScreenCursorHandler());
        elementRegistrar.register(new CreativeInventoryScreenCursorHandler());
        elementRegistrar.register(new BookEditScreenCursorHandler());
        elementRegistrar.register(new EnchantmentScreenCursorHandler());
        elementRegistrar.register(new StonecutterScreenCursorHandler());
        elementRegistrar.register(new LoomScreenCursorHandler());
        elementRegistrar.register(new CrafterScreenCursorHandler());
        elementRegistrar.register(new AdvancementsScreenCursorHandler());
    }

    private CursorType elementToBusy(GuiEventListener ignoreElement, double ignoreMouseX, double ignoreMouseY) {
        return CursorType.BUSY;
    }

    private CursorType buttonWidgetCursor(AbstractButton button, double mouseX, double mouseY) {
        if (button.visible && button.isHovered()) {
            return inactiveButtonCursor(button);
        }
        return CursorType.DEFAULT;
    }

    private CursorType tabButtonWidgetCursor(TabButton tabButton, double mouseX, double mouseY) {
        return tabButton.active && tabButton.visible && tabButton.isHovered() && !tabButton.isSelected()
                ? CursorType.POINTER
                : CursorType.DEFAULT;
    }

    private CursorType sliderWidgetCursor(AbstractSliderButton slider, double mouseX, double mouseY) {
        if (slider.isFocused() && (CursorTypeUtil.isLeftClickHeld() || CursorTypeUtil.isGrabbing())) {
            return CursorType.GRABBING;
        }

        return slider.visible ? inactiveButtonCursor(slider) : CursorType.DEFAULT;
    }

    private CursorType textFieldWidgetCursor(AbstractWidget textField, double mouseX, double mouseY) {
        return textField.visible && textField.active && textField.isHovered() ? CursorType.TEXT : CursorType.DEFAULT;
    }

    private static CursorType inactiveButtonCursor(AbstractWidget button) {
        if (button.active) {
            return CursorType.POINTER;
        } else if (MinecraftCursor.CONFIG.isInactiveWidgetsEnabled()) {
            return CursorType.NOT_ALLOWED;
        }
        return CursorType.DEFAULT;
    }
}
