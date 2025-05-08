package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.compat.ExternalCursorTracker;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfigLoader;
import io.github.fishstiz.minecraftcursor.impl.CursorControllerImpl;
import io.github.fishstiz.minecraftcursor.provider.CursorControllerProvider;
import io.github.fishstiz.minecraftcursor.impl.MinecraftCursorInitializerImpl;
import io.github.fishstiz.minecraftcursor.platform.Services;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinecraftCursor {
    public static final String MOD_ID = "minecraft-cursor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final CursorConfig CONFIG = CursorConfigLoader.fromFile(Services.PLATFORM.getConfigDir().resolve(MOD_ID + ".json").toFile());
    private static final CursorTypeResolver RESOLVER = new CursorTypeResolver();
    private CursorType singleCycleCursor;
    private Screen visibleNonCurrentScreen;

    MinecraftCursor() {
    }

    public void init() {
        new MinecraftCursorInitializerImpl().init(CursorManager.INSTANCE, RESOLVER);

        Services.PLATFORM.getEntrypoints().forEach(entrypoint -> {
            try {
                entrypoint.init(CursorManager.INSTANCE, RESOLVER);
            } catch (LinkageError | Exception e) {
                LOGGER.error("[minecraft-cursor] Skipping invalid implementation of MinecraftCursorInitializer");
            }
        });

        CursorControllerProvider.init(new CursorControllerImpl(this));
    }

    public void onScreenInit(Minecraft minecraft, Screen screen) {
        RESOLVER.lastFailedElement = "";

        if (minecraft.screen == null) {
            CursorManager.INSTANCE.setCurrentCursor(CursorType.DEFAULT);
            visibleNonCurrentScreen = screen;
            return;
        }

        visibleNonCurrentScreen = null;
    }

    public void onScreenRender(Minecraft minecraft, int mouseX, int mouseY) {
        if (ExternalCursorTracker.get().isCustom()) return;

        if (minecraft.screen != null) {
            CursorManager.INSTANCE.setCurrentCursor(resolveCursorType(minecraft.screen, mouseX, mouseY));
        }
    }

    public void onClientTick(Minecraft minecraft) {
        if (ExternalCursorTracker.get().isCustom()) return;

        if (minecraft.screen == null && visibleNonCurrentScreen != null && !minecraft.mouseHandler.isMouseGrabbed()) {
            double scale = minecraft.getWindow().getGuiScale();
            double mouseX = minecraft.mouseHandler.xpos() / scale;
            double mouseY = minecraft.mouseHandler.ypos() / scale;
            CursorManager.INSTANCE.setCurrentCursor(resolveCursorType(visibleNonCurrentScreen, mouseX, mouseY));
        } else if (minecraft.screen == null && visibleNonCurrentScreen == null) {
            CursorManager.INSTANCE.setCurrentCursor(ExternalCursorTracker.get().getCursorOrDefault());
        }
    }

    private CursorType resolveCursorType(Screen screen, double mouseX, double mouseY) {
        if (!CursorManager.INSTANCE.isAdaptive()) {
            return CursorType.DEFAULT;
        }

        if (singleCycleCursor != null) {
            CursorType cursorType = singleCycleCursor;
            singleCycleCursor = null;
            return cursorType;
        }

        CursorType externalCursor = ExternalCursorTracker.get().getCursorOrDefault();
        if (!externalCursor.isDefault()) {
            return externalCursor;
        }

        if (CursorTypeUtil.isGrabbing()) {
            return CursorType.GRABBING;
        }

        CursorType cursorType = RESOLVER.resolve(screen, mouseX, mouseY);

        if (!cursorType.isDefault()) {
            return cursorType;
        }

        for (GuiEventListener child : screen.children()) {
            if (child.isMouseOver(mouseX, mouseY)) {
                cursorType = RESOLVER.resolve(child, mouseX, mouseY);
                if (!cursorType.isDefault()) {
                    return cursorType;
                }
            }
        }

        return CursorType.DEFAULT;
    }

    public void setSingleCycleCursor(CursorType cursorType) {
        singleCycleCursor = cursorType;
    }
}
