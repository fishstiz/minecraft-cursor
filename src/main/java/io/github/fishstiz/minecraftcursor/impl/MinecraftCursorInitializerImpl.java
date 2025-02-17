package io.github.fishstiz.minecraftcursor.impl;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.CursorTypeRegistrar;
import io.github.fishstiz.minecraftcursor.api.ElementRegistrar;
import io.github.fishstiz.minecraftcursor.api.MinecraftCursorInitializer;
import io.github.fishstiz.minecraftcursor.cursorhandler.ingame.*;
import io.github.fishstiz.minecraftcursor.cursorhandler.modmenu.ModScreenCursorHandler;
import io.github.fishstiz.minecraftcursor.cursorhandler.multiplayer.MultiplayerServerListWidgetCursorHandler;
import io.github.fishstiz.minecraftcursor.cursorhandler.world.WorldListWidgetCursorHandler;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.client.gui.widget.*;

public class MinecraftCursorInitializerImpl implements MinecraftCursorInitializer {
    @Override
    public void init(CursorTypeRegistrar cursorRegistrar, ElementRegistrar elementRegistrar) {
        cursorRegistrar.register(
                CursorType.DEFAULT,
                CursorType.POINTER,
                CursorType.GRABBING,
                CursorType.TEXT,
                CursorType.SHIFT,
                CursorType.BUSY
        );

        elementRegistrar.register(MessageScreen.class, MinecraftCursorInitializerImpl::elementToBusy);
        elementRegistrar.register(DownloadingTerrainScreen.class, MinecraftCursorInitializerImpl::elementToBusy);
        elementRegistrar.register(ProgressScreen.class, MinecraftCursorInitializerImpl::elementToBusy);
        elementRegistrar.register(LevelLoadingScreen.class, MinecraftCursorInitializerImpl::elementToBusy);
        elementRegistrar.register(PressableWidget.class, MinecraftCursorInitializerImpl::clickableWidgetCursor);
        elementRegistrar.register(TabButtonWidget.class, MinecraftCursorInitializerImpl::tabButtonWidgetCursor);
        elementRegistrar.register(SliderWidget.class, MinecraftCursorInitializerImpl::sliderWidgetCursor);
        elementRegistrar.register(TextFieldWidget.class, MinecraftCursorInitializerImpl::textFieldWidgetCursor);
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
        elementRegistrar.register(new MerchantScreenButtonCursorHandler());
        elementRegistrar.register(new AdvancementsScreenCursorHandler());

        try {
            if (FabricLoader.getInstance().isModLoaded("modmenu")) {
                elementRegistrar.register(new ModScreenCursorHandler());
                elementRegistrar.register("com.terraformersmc.modmenu.gui.widget.DescriptionListWidget$MojangCreditsEntry", ElementRegistrar::elementToPointer);
                elementRegistrar.register("com.terraformersmc.modmenu.gui.widget.DescriptionListWidget$LinkEntry", ElementRegistrar::elementToPointer);
            }
        } catch (LinkageError | Exception e) {
            MinecraftCursor.LOGGER.warn("Could not register cursor type for Mod Menu");
        }
    }

    private static <T extends Element> CursorType elementToBusy(T ignoreElement, double ignoreMouseX, double ignoreMouseY) {
        return CursorType.BUSY;
    }

    private static <T extends ClickableWidget> CursorType clickableWidgetCursor(T clickable, double mouseX, double mouseY) {
        return clickable.active && clickable.visible ? CursorType.POINTER : CursorType.DEFAULT;
    }

    private static <T extends TabButtonWidget> CursorType tabButtonWidgetCursor(T tabButton, double mouseX, double mouseY) {
        return tabButton.active && tabButton.visible && !tabButton.isCurrentTab() ?
                CursorType.POINTER : CursorType.DEFAULT;
    }

    private static <T extends SliderWidget> CursorType sliderWidgetCursor(T slider, double mouseX, double mouseY) {
        if (slider.isFocused() && (CursorTypeUtil.isLeftClickHeld() || CursorTypeUtil.isGrabbing())) {
            return CursorType.GRABBING;
        }
        return slider.active && slider.visible ? CursorType.POINTER : CursorType.DEFAULT;
    }

    private static <T extends TextFieldWidget> CursorType textFieldWidgetCursor(T textField, double mouseX, double mouseY) {
        return textField.visible ? CursorType.TEXT : CursorType.DEFAULT;
    }
}
