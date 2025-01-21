package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.config.CursorConfigLoader;
import io.github.fishstiz.minecraftcursor.config.CursorConfigService;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;

public class MinecraftCursorClient implements ClientModInitializer {
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static final CursorConfigService CONFIG =
            new CursorConfigService(String.format("config/%s%s", MinecraftCursor.MOD_ID, CursorConfigLoader.FILE_EXTENSION));
    public static final CursorManager CURSOR_MANAGER = new CursorManager(CONFIG, CLIENT);
    public static final CursorTypeRegistry CURSOR_REGISTRY = new CursorTypeRegistry();

    @Override
    public void onInitializeClient() {
        CursorResourceReloadListener resourceReloadListener =
                new CursorResourceReloadListener(CURSOR_MANAGER, MinecraftCursor.MOD_ID, CONFIG);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(resourceReloadListener);

        ClientTickEvents.START_CLIENT_TICK.register(this::tick);
    }

    public void tick(MinecraftClient client) {
        if (client.currentScreen == null) {
            return;
        }
        if (!CURSOR_MANAGER.isAdaptive()) {
            CURSOR_MANAGER.setCurrentCursor(CursorType.DEFAULT);
            return;
        }
        if (client.currentScreen.isDragging()
                && CURSOR_MANAGER.getCurrentCursor().getType() == CursorType.GRABBING) {
            return;
        }

        double scale = client.getWindow().getScaleFactor();
        double x = client.mouse.getX() / scale;
        double y = client.mouse.getY() / scale;

        CursorType screenCursorType = CURSOR_REGISTRY.getCursorType(client.currentScreen, x, y);
        CursorType cursorType = (screenCursorType != CursorType.DEFAULT)
                ? screenCursorType
                : client.currentScreen.hoveredElement(x, y)
                .map(element -> CURSOR_REGISTRY.getCursorType(element, x, y))
                .orElse(CursorType.DEFAULT);

        CURSOR_MANAGER.setCurrentCursor(cursorType);
    }
}
