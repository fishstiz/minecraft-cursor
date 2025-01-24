package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.config.CursorConfigLoader;
import io.github.fishstiz.minecraftcursor.config.CursorConfigService;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.ResourceType;

import java.util.ArrayList;
import java.util.List;

public class MinecraftCursorClient implements ClientModInitializer {
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static final CursorConfigService CONFIG =
            new CursorConfigService(String.format("config/%s%s", MinecraftCursor.MOD_ID, CursorConfigLoader.FILE_EXTENSION));
    public static final CursorManager CURSOR_MANAGER = new CursorManager(CONFIG, CLIENT);
    public static final CursorTypeRegistry CURSOR_REGISTRY = new CursorTypeRegistry();
    public static final List<Element> SCREEN_ELEMENTS = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        CursorResourceReloadListener resourceReloadListener =
                new CursorResourceReloadListener(CURSOR_MANAGER, MinecraftCursor.MOD_ID, CONFIG);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(resourceReloadListener);

        ScreenEvents.BEFORE_INIT.register((MinecraftClient client, Screen screen, int width, int height) -> {
            if (client.currentScreen == null) return;
            SCREEN_ELEMENTS.clear();
            ScreenEvents.afterRender(client.currentScreen).register(this::afterRender);
        });
    }

    public void afterRender(Screen currentScreen, DrawContext context, int mouseX, int mouseY, float tickDelta) {
        if (!CURSOR_MANAGER.isAdaptive()) {
            CURSOR_MANAGER.setCurrentCursor(CursorType.DEFAULT);
            return;
        }
        if (currentScreen.isDragging() && CURSOR_MANAGER.getCurrentCursor().getType() == CursorType.GRABBING) {
            return;
        }

        CursorType cursorType = CursorType.DEFAULT;
        for (Element element : SCREEN_ELEMENTS) {
            if (element.isMouseOver(mouseX, mouseY)) {
                cursorType = CURSOR_REGISTRY.getCursorType(element, mouseX, mouseY);
            }
        }
        if (cursorType == CursorType.DEFAULT) {
            cursorType = CURSOR_REGISTRY.getCursorType(currentScreen, mouseX, mouseY);
        }
        if (cursorType == CursorType.DEFAULT) {
            cursorType = currentScreen.hoveredElement(mouseX, mouseY)
                    .map(element -> CURSOR_REGISTRY.getCursorType(element, mouseX, mouseY))
                    .orElse(CursorType.DEFAULT);
        }

        CURSOR_MANAGER.setCurrentCursor(cursorType);
    }
}
