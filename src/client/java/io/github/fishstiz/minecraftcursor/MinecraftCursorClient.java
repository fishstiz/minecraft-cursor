package io.github.fishstiz.minecraftcursor;

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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.ResourceType;

import java.util.HashSet;

public class MinecraftCursorClient implements ClientModInitializer {
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static final CursorConfigService CONFIG =
            new CursorConfigService(String.format("config/%s%s", MinecraftCursor.MOD_ID, CursorConfigLoader.FILE_EXTENSION));
    public static final CursorManager CURSOR_MANAGER = new CursorManager(CONFIG, CLIENT);
    public static final CursorTypeRegistry CURSOR_REGISTRY = new CursorTypeRegistry();
    public static final HashSet<Element> SCREEN_ELEMENTS = new HashSet<>();

    private Screen visibleNonCurrentScreen;

    @Override
    public void onInitializeClient() {
        CursorResourceReloadListener resourceReloadListener =
                new CursorResourceReloadListener(CURSOR_MANAGER, MinecraftCursor.MOD_ID, CONFIG);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(resourceReloadListener);

        ScreenEvents.BEFORE_INIT.register((MinecraftClient client, Screen screen, int width, int height) -> {
            SCREEN_ELEMENTS.clear();

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

    public void afterRenderScreen(Screen currentScreen, DrawContext context, int mouseX, int mouseY, float tickDelta) {
        CURSOR_MANAGER.setCurrentCursor(getCursorType(currentScreen, mouseX, mouseY));
    }

    public void tick(MinecraftClient client) {
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

        CursorType cursorType = CURSOR_REGISTRY.getCursorType(currentScreen, mouseX, mouseY);
        cursorType = cursorType != CursorType.DEFAULT ? cursorType
                : currentScreen.hoveredElement(mouseX, mouseY)
                .map(element -> CURSOR_REGISTRY.getCursorType(element, mouseX, mouseY))
                .orElse(CursorType.DEFAULT);

        return cursorType;
    }
}
