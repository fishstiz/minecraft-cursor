package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.compat.ExternalCursorTracker;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfigLoader;
import io.github.fishstiz.minecraftcursor.inspect.ElementInspector;
import io.github.fishstiz.minecraftcursor.impl.CursorControllerImpl;
import io.github.fishstiz.minecraftcursor.impl.MinecraftCursorInitializerImpl;
import io.github.fishstiz.minecraftcursor.provider.CursorControllerProvider;
import io.github.fishstiz.minecraftcursor.platform.Services;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public final class MinecraftCursor {
    public static final String MOD_ID = "minecraft-cursor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final CursorConfig CONFIG = CursorConfigLoader.fromFile(Services.PLATFORM.getConfigDir().resolve(MOD_ID + ".json").toFile());
    private static final CursorTypeResolver RESOLVER = new CursorTypeResolver();
    private static final CursorControllerImpl CONTROLLER = new CursorControllerImpl();
    private static Screen visibleScreen;

    private MinecraftCursor() {
    }

    static void init() {
        new MinecraftCursorInitializerImpl().init(CursorManager.INSTANCE, RESOLVER);

        Services.PLATFORM.getEntrypoints().forEach(entrypoint -> {
            try {
                entrypoint.init(CursorManager.INSTANCE, RESOLVER);
            } catch (LinkageError | Exception e) {
                LOGGER.error("[minecraft-cursor] Skipping invalid implementation of MinecraftCursorInitializer");
            }
        });

        CursorControllerProvider.init(CONTROLLER);
    }

    static void onScreenInit(Minecraft minecraft, Screen screen) {
        RESOLVER.lastFailedElement = "";

        if (minecraft.screen == null) {
            CursorManager.INSTANCE.setCurrentCursor(CursorType.DEFAULT);
            visibleScreen = screen;
            return;
        }

        visibleScreen = null;
    }

    static void onScreenRender(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY) {
        ElementInspector inspector = RESOLVER.getInspector();
        if (inspector.isEnabled()) {
            inspector.renderDeepest(minecraft, guiGraphics, minecraft.screen, mouseX, mouseY);
            inspector.renderHovered(minecraft, guiGraphics);
            inspector.renderCacheSize(minecraft, guiGraphics);
        }

        if (ExternalCursorTracker.get().isCustom()) return;

        if (minecraft.screen != null) {
            CursorManager.INSTANCE.setCurrentCursor(resolveCursorType(minecraft.screen, mouseX, mouseY));
        }
    }

    static void onClientTick(Minecraft minecraft) {
        if (ExternalCursorTracker.get().isCustom()) return;

        if (minecraft.screen == null && visibleScreen != null && !minecraft.mouseHandler.isMouseGrabbed()) {
            double scale = minecraft.getWindow().getGuiScale();
            double mouseX = minecraft.mouseHandler.xpos() / scale;
            double mouseY = minecraft.mouseHandler.ypos() / scale;
            CursorManager.INSTANCE.setCurrentCursor(resolveCursorType(visibleScreen, mouseX, mouseY));
        } else if (minecraft.screen == null && visibleScreen == null) {
            CursorManager.INSTANCE.setCurrentCursor(ExternalCursorTracker.get().getCursorOrDefault());
        }
    }

    private static CursorType resolveCursorType(Screen screen, double mouseX, double mouseY) {
        if (!CursorManager.INSTANCE.isAdaptive()) {
            return CursorType.DEFAULT;
        }

        if (CONTROLLER.hasTransientCursor()) {
            return CONTROLLER.consumeTransientCursor();
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

        Optional<GuiEventListener> child = screen.getChildAt(mouseX, mouseY);

        if (child.isPresent()) {
            return RESOLVER.resolve(child.get(), mouseX, mouseY);
        }

        return CursorType.DEFAULT;
    }

    public static void toggleInspect() {
        RESOLVER.toggleInspector();
    }
}
