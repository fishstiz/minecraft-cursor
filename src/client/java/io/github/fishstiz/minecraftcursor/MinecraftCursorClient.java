package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.MinecraftCursorInitializer;
import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.config.CursorConfigLoader;
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

import java.io.File;

public class MinecraftCursorClient implements ClientModInitializer {
    private static final CursorTypeRegistry CURSOR_REGISTRY = new CursorTypeRegistry();
    private static final CursorTypeResolver CURSOR_RESOLVER = new CursorTypeResolver();
    public static final CursorManager CURSOR_MANAGER = new CursorManager();
    public static final CursorConfig CONFIG = CursorConfigLoader.fromFile(new File(String.format("%s/%s.json",
            FabricLoader.getInstance().getConfigDir(),
            MinecraftCursor.MOD_ID
    )));
    private static CursorType singleCycleCursor;
    private Screen visibleNonCurrentScreen;

    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance().getEntrypoints(MinecraftCursor.MOD_ID, MinecraftCursorInitializer.class).forEach(
                entrypoint -> entrypoint.init(CURSOR_REGISTRY, CURSOR_RESOLVER)
        );

        CursorResourceReloadListener resourceReloadListener = new CursorResourceReloadListener(
                CURSOR_MANAGER, MinecraftCursor.MOD_ID, CONFIG
        );

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(resourceReloadListener);

        ScreenEvents.BEFORE_INIT.register((MinecraftClient client, Screen screen, int width, int height) -> {
            if (client.currentScreen == null) {
                CURSOR_MANAGER.setCurrentCursor(CursorType.DEFAULT);
                visibleNonCurrentScreen = screen;
                return;
            }

            visibleNonCurrentScreen = null;
            ScreenEvents.afterRender(client.currentScreen).register(this::afterRenderScreen);
        });

        ClientTickEvents.START_CLIENT_TICK.register(this::tick);
    }

    private void afterRenderScreen(Screen currentScreen, DrawContext context, int mouseX, int mouseY, float tickDelta) {
        CURSOR_MANAGER.setCurrentCursor(getCursorType(currentScreen, mouseX, mouseY));
    }

    private void tick(MinecraftClient client) {
        if (client.currentScreen == null && visibleNonCurrentScreen != null && !client.mouse.isCursorLocked()) {
            double scale = client.getWindow().getScaleFactor();
            double mouseX = client.mouse.getX() / scale;
            double mouseY = client.mouse.getY() / scale;
            CURSOR_MANAGER.setCurrentCursor(getCursorType(visibleNonCurrentScreen, mouseX, mouseY));
        }
    }

    private CursorType getCursorType(Screen currentScreen, double mouseX, double mouseY) {
        if (!CURSOR_MANAGER.isAdaptive()) return CursorType.DEFAULT;

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
}
