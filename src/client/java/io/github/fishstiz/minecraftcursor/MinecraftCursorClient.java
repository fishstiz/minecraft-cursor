package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.MinecraftCursorApi;
import io.github.fishstiz.minecraftcursor.config.CursorConfigLoader;
import io.github.fishstiz.minecraftcursor.config.CursorConfigService;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
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

public class MinecraftCursorClient implements ClientModInitializer {
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static final CursorConfigService CONFIG =
            new CursorConfigService(String.format("config/%s%s", MinecraftCursor.MOD_ID, CursorConfigLoader.FILE_EXTENSION));
    public static final CursorManager CURSOR_MANAGER = new CursorManager(CONFIG, CLIENT);
    public static final CursorTypeRegistry CURSOR_REGISTRY = new CursorTypeRegistry();
    private static MinecraftCursorClient instance;
    private Screen visibleNonCurrentScreen;
    private CursorType singleCycleCursor;

    @Override
    public void onInitializeClient() {
        instance = this;

        FabricLoader.getInstance().getEntrypoints(MinecraftCursor.MOD_ID, MinecraftCursorApi.class).forEach(
                entrypoint -> entrypoint.init(CURSOR_REGISTRY)
        );

        CursorResourceReloadListener resourceReloadListener = new CursorResourceReloadListener(
                CURSOR_MANAGER, MinecraftCursor.MOD_ID, CONFIG);
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
            CursorType singleCycleCursor = this.singleCycleCursor;
            this.singleCycleCursor = null;
            return singleCycleCursor;
        }

        CursorType cursorType = CURSOR_REGISTRY.getCursorType(currentScreen, mouseX, mouseY);
        cursorType = cursorType != CursorType.DEFAULT ? cursorType
                : currentScreen.hoveredElement(mouseX, mouseY)
                .map(element -> CURSOR_REGISTRY.getCursorType(element, mouseX, mouseY))
                .orElse(CursorType.DEFAULT);

        return cursorType;
    }

    public static void setSingleCycleCursor(CursorType cursorType) {
        instance.singleCycleCursor = cursorType;
    }
}
