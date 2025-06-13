package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.compat.ExternalCursorTracker;
import io.github.fishstiz.minecraftcursor.config.Config;
import io.github.fishstiz.minecraftcursor.config.ConfigLoader;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.cursor.resolver.CursorTypeResolver;
import io.github.fishstiz.minecraftcursor.cursor.resolver.ElementWalker;
import io.github.fishstiz.minecraftcursor.impl.CursorControllerImpl;
import io.github.fishstiz.minecraftcursor.impl.MinecraftCursorInitializerImpl;
import io.github.fishstiz.minecraftcursor.provider.CursorControllerProvider;
import io.github.fishstiz.minecraftcursor.platform.Services;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import io.github.fishstiz.minecraftcursor.config.Flag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MinecraftCursor {
    public static final String MOD_ID = "minecraft-cursor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Config CONFIG = ConfigLoader.load(Services.PLATFORM.getConfigDir().resolve(MOD_ID + ".json").toFile());
    private static final CursorControllerImpl CONTROLLER = new CursorControllerImpl();
    private static Screen visibleHudScreen;

    private MinecraftCursor() {
    }

    static void init() {
        new MinecraftCursorInitializerImpl().init(CursorManager.INSTANCE, CursorTypeResolver.INSTANCE);

        Services.PLATFORM.getEntrypoints().forEach(entrypoint -> {
            try {
                entrypoint.init(CursorManager.INSTANCE, CursorTypeResolver.INSTANCE);
            } catch (LinkageError | Exception e) {
                LOGGER.error("[minecraft-cursor] Skipping invalid implementation of MinecraftCursorInitializer");
            }
        });

        CursorControllerProvider.init(CONTROLLER);

        if (!Services.PLATFORM.isFabric()) {
            Flag.REMAP.disable(); // Unsupported in Forge/NeoForge
        }
    }

    static void onScreenInit(Minecraft minecraft, Screen screen) {
        if (minecraft.screen == null) {
            CursorManager.INSTANCE.setCurrentCursor(CursorType.DEFAULT);
            visibleHudScreen = screen;
            return;
        }
        visibleHudScreen = null;
    }

    static void onScreenRender(Minecraft minecraft, Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY) {
        visibleHudScreen = null;

        CursorTypeResolver.INSTANCE.getInspector().render(minecraft, screen, guiGraphics, mouseX, mouseY);

        if (ExternalCursorTracker.get().isCustom()) return;

        CursorManager.INSTANCE.setCurrentCursor(resolveCursorType(screen, mouseX, mouseY));
    }

    static void onClientTick(Minecraft minecraft) {
        if (ExternalCursorTracker.get().isCustom()) return;

        if (minecraft.screen == null && visibleHudScreen != null && !minecraft.mouseHandler.isMouseGrabbed()) {
            double scale = minecraft.getWindow().getGuiScale();
            double mouseX = minecraft.mouseHandler.xpos() / scale;
            double mouseY = minecraft.mouseHandler.ypos() / scale;
            CursorManager.INSTANCE.setCurrentCursor(resolveCursorType(visibleHudScreen, mouseX, mouseY));
        } else if (minecraft.screen == null && visibleHudScreen == null) {
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

        CursorType cursorType = CursorTypeResolver.INSTANCE.resolve(screen, mouseX, mouseY);
        if (!cursorType.isDefault()) {
            return cursorType;
        }

        GuiEventListener child = ElementWalker.findFirst(screen, mouseX, mouseY);
        if (child != null) {
            return CursorTypeResolver.INSTANCE.resolve(child, mouseX, mouseY);
        }

        return CursorType.DEFAULT;
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
