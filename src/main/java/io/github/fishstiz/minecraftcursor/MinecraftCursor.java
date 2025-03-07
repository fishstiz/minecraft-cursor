package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.MinecraftCursorInitializer;
import io.github.fishstiz.minecraftcursor.compat.ExternalCursorTracker;
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
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class MinecraftCursor implements ClientModInitializer {
    public static final String MOD_ID = "minecraft-cursor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final CursorConfig CONFIG = CursorConfigLoader
            .fromFile(new File(FabricLoader.getInstance().getConfigDir().toString(), MOD_ID + ".json"));

    private static MinecraftCursor instance;
    private final AtomicReference<CursorType> singleCycleCursor = new AtomicReference<>();
    private Screen visibleNonCurrentScreen;

    @Override
    public void onInitializeClient() {
        instance = this;

        new MinecraftCursorInitializerImpl().init(CursorManager.INSTANCE, CursorTypeResolver.INSTANCE);
        FabricLoader.getInstance().getEntrypointContainers(MOD_ID, MinecraftCursorInitializer.class).forEach(entrypoint -> {
            try {
                entrypoint.getEntrypoint().init(CursorManager.INSTANCE, CursorTypeResolver.INSTANCE);
            } catch (LinkageError | Exception e) {
                LOGGER.error(
                        "Invalid implementation of MinecraftCursorInitializer in mod: {}",
                        entrypoint.getProvider().getMetadata().getId()
                );
            }
        });

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(CursorResourceReloadListener.INSTANCE);
        CursorControllerProvider.init(CursorControllerImpl.INSTANCE);

        ScreenEvents.BEFORE_INIT.register(this::beforeScreenInit);
        ClientTickEvents.START_CLIENT_TICK.register(this::tick);
    }

    private void beforeScreenInit(MinecraftClient client, Screen screen, int width, int height) {
        if (client.currentScreen == null) {
            CursorManager.INSTANCE.setCurrentCursor(CursorType.DEFAULT);
            visibleNonCurrentScreen = screen;
            return;
        }
        visibleNonCurrentScreen = null;
        ScreenEvents.afterRender(client.currentScreen).register(this::afterRenderScreen);
    }

    private void afterRenderScreen(Screen currentScreen, DrawContext context, int mouseX, int mouseY, float tickDelta) {
        CursorManager.INSTANCE.setCurrentCursor(getCursorType(currentScreen, mouseX, mouseY));
    }

    private void tick(MinecraftClient client) {
        if (client.currentScreen == null && visibleNonCurrentScreen != null && !client.mouse.isCursorLocked()) {
            double scale = client.getWindow().getScaleFactor();
            double mouseX = client.mouse.getX() / scale;
            double mouseY = client.mouse.getY() / scale;
            CursorManager.INSTANCE.setCurrentCursor(getCursorType(visibleNonCurrentScreen, mouseX, mouseY));
        } else if (client.currentScreen == null && visibleNonCurrentScreen == null) {
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
            Optional<Element> hoveredElement = currentScreen.hoveredElement(mouseX, mouseY);
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
