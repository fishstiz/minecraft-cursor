package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.MinecraftCursorInitializer;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfigLoader;
import io.github.fishstiz.minecraftcursor.impl.CursorControllerImpl;
import io.github.fishstiz.minecraftcursor.impl.CursorControllerProvider;
import io.github.fishstiz.minecraftcursor.impl.MinecraftCursorInitializerImpl;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MinecraftCursor implements ClientModInitializer {
    public static final String MOD_ID = "minecraft-cursor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final CursorTypeRegistry CURSOR_REGISTRY = new CursorTypeRegistry();
    private static final CursorTypeResolver CURSOR_RESOLVER = new CursorTypeResolver();
    public static final CursorConfig CONFIG = CursorConfigLoader
            .fromFile(new File(FabricLoader.getInstance().getConfigDir().toString(), MOD_ID + ".json"));
    private static CursorManager cursorManager;
    private static CursorType singleCycleCursor;
    private Screen visibleNonCurrentScreen;

    @Override
    public void onInitializeClient() {
        new MinecraftCursorInitializerImpl().init(CURSOR_REGISTRY, CURSOR_RESOLVER);
        FabricLoader.getInstance().getEntrypointContainers(MOD_ID, MinecraftCursorInitializer.class).forEach(entrypoint -> {
            try {
                entrypoint.getEntrypoint().init(CURSOR_REGISTRY, CURSOR_RESOLVER);
            } catch (LinkageError | Exception e) {
                LOGGER.error(
                        "Invalid implementation of MinecraftCursorInitializer in mod: {}",
                        entrypoint.getProvider().getMetadata().getId()
                );
            }
        });

        initCursorManager();
        CursorResourceReloadListener reloadListener = new CursorResourceReloadListener(cursorManager, MOD_ID, CONFIG);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(reloadListener);
        CursorControllerProvider.init(CursorControllerImpl.getInstance());

        ScreenEvents.BEFORE_INIT.register(this::beforeScreenInit);
        ClientTickEvents.START_CLIENT_TICK.register(this::tick);
    }

    private static void initCursorManager() {
        cursorManager = new CursorManager(MinecraftClient.getInstance());
    }

    private void beforeScreenInit(MinecraftClient client, Screen screen, int width, int height) {
        if (client.currentScreen == null) {
            cursorManager.setCurrentCursor(CursorType.DEFAULT);
            visibleNonCurrentScreen = screen;
            return;
        }
        visibleNonCurrentScreen = null;
        ScreenEvents.afterRender(client.currentScreen).register(this::afterRenderScreen);
    }

    private void afterRenderScreen(Screen currentScreen, DrawContext context, int mouseX, int mouseY, float tickDelta) {
        cursorManager.setCurrentCursor(getCursorType(currentScreen, mouseX, mouseY));
    }

    private void tick(MinecraftClient client) {
        if (client.currentScreen == null && visibleNonCurrentScreen != null && !client.mouse.isCursorLocked()) {
            double scale = client.getWindow().getScaleFactor();
            double mouseX = client.mouse.getX() / scale;
            double mouseY = client.mouse.getY() / scale;
            cursorManager.setCurrentCursor(getCursorType(visibleNonCurrentScreen, mouseX, mouseY));
        }
    }

    private CursorType getCursorType(Screen currentScreen, double mouseX, double mouseY) {
        if (!cursorManager.isAdaptive()) return CursorType.DEFAULT;

        if (CursorTypeUtil.isGrabbing()) return CursorType.GRABBING;

        if (singleCycleCursor != null) {
            CursorType temp = singleCycleCursor;
            setSingleCycleCursor(null);
            return temp;
        }

        CursorType cursorType = CURSOR_RESOLVER.getCursorType(currentScreen, mouseX, mouseY);
        cursorType = cursorType != CursorType.DEFAULT ? cursorType
                : currentScreen.hoveredElement(mouseX, mouseY)
                .map(element -> CURSOR_RESOLVER.getCursorType(element, mouseX, mouseY))
                .orElse(CursorType.DEFAULT);

        return cursorType;
    }

    public static void setSingleCycleCursor(CursorType cursorType) {
        singleCycleCursor = cursorType;
    }

    public static CursorManager getCursorManager() {
        if (cursorManager == null) {
            throw new IllegalStateException("Cursor Manager not yet initialized");
        }
        return cursorManager;
    }
}
