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

import java.io.File;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class MinecraftCursor {
    public static final String MOD_ID = "minecraft-cursor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final Minecraft CLIENT = Minecraft.getInstance();
    public static final CursorConfig CONFIG = CursorConfigLoader
            .fromFile(new File(Services.PLATFORM.getConfigDir(), MOD_ID + ".json"));

    private static MinecraftCursor instance;
    private final AtomicReference<CursorType> singleCycleCursor = new AtomicReference<>();
    private Screen visibleNonCurrentScreen;

    private MinecraftCursor() {
    }

    public static void init() {
        instance = new MinecraftCursor();

        new MinecraftCursorInitializerImpl().init(CursorManager.INSTANCE, CursorTypeResolver.INSTANCE);

        Services.PLATFORM.getEntrypoints().forEach(entrypoint -> {
            try {
                entrypoint.init(CursorManager.INSTANCE, CursorTypeResolver.INSTANCE);
            } catch (LinkageError | Exception e) {
                LOGGER.error("Invalid implementation of MinecraftCursorInitializer");
            }
        });

        CursorControllerProvider.init(CursorControllerImpl.INSTANCE);
    }

    public void beforeScreenInit(Screen screen) {
        if (CLIENT.screen == null) {
            CursorManager.INSTANCE.setCurrentCursor(CursorType.DEFAULT);
            visibleNonCurrentScreen = screen;
            return;
        }
        visibleNonCurrentScreen = null;
    }

    public void afterRenderScreen(int mouseX, int mouseY) {
        if (CLIENT.screen != null) {
            CursorManager.INSTANCE.setCurrentCursor(getCursorType(CLIENT.screen, mouseX, mouseY));
        }
    }

    public void tick() {
        if (CLIENT.screen == null && visibleNonCurrentScreen != null && !CLIENT.mouseHandler.isMouseGrabbed()) {
            double scale = CLIENT.getWindow().getGuiScale();
            double mouseX = CLIENT.mouseHandler.xpos() / scale;
            double mouseY = CLIENT.mouseHandler.ypos() / scale;
            CursorManager.INSTANCE.setCurrentCursor(getCursorType(visibleNonCurrentScreen, mouseX, mouseY));
        } else if (CLIENT.screen == null && visibleNonCurrentScreen == null) {
            CursorManager.INSTANCE.setCurrentCursor(ExternalCursorTracker.getCursorOrDefault());
        }
    }

    private CursorType getCursorType(Screen currentScreen, double mouseX, double mouseY) {
        if (!CursorManager.INSTANCE.isAdaptive()) return CursorType.DEFAULT;

        if (CursorTypeUtil.isGrabbing()) return CursorType.GRABBING;

        if (singleCycleCursor.get() != null) {
            CursorType cursorType = singleCycleCursor.get();
            singleCycleCursor.set(null);
            return cursorType;
        }

        CursorType externalCursor = ExternalCursorTracker.getCursorOrDefault();
        if (externalCursor != CursorType.DEFAULT) return externalCursor;

        CursorType cursorType = CursorTypeResolver.INSTANCE.resolveCursorType(currentScreen, mouseX, mouseY);

        if (cursorType == CursorType.DEFAULT) {
            Optional<GuiEventListener> hoveredElement = currentScreen.getChildAt(mouseX, mouseY);
            if (hoveredElement.isPresent()) {
                cursorType = CursorTypeResolver.INSTANCE.resolveCursorType(hoveredElement.get(), mouseX, mouseY);
            }
        }

        return cursorType;
    }

    public static MinecraftCursor getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MinecraftCursor not yet initialized.");
        }
        return instance;
    }

    public synchronized void setSingleCycleCursor(CursorType cursorType) {
        singleCycleCursor.set(cursorType);
    }
}
