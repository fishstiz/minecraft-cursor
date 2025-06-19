package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorController;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p><b>{@link CursorType} Priority:</b></p>
 * <ol>
 *   <li><b>External override:</b> {@link ExternalCursorTracker#isCustom()}</li>
 *   <li><b>Manual override:</b> {@link CursorController#overrideCursor}</li>
 *   <li><b>Transient cursor:</b> {@link CursorController#setSingleCycleCursor}</li>
 *   <li><b>Contextual resolution:</b> {@link CursorTypeResolver#resolve} based on screen or element</li>
 *   <li><b>Fallback cursor:</b> {@link CursorType#DEFAULT} if all else fails</li>
 * </ol>
 */
public final class MinecraftCursor {
    public static final String MOD_ID = "minecraft-cursor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Config CONFIG = ConfigLoader.load(Services.PLATFORM.getConfigDir().resolve(MOD_ID + ".json").toFile());
    private static final CursorControllerImpl CONTROLLER = new CursorControllerImpl();
    private static @Nullable CursorType deferredCursorType;

    private MinecraftCursor() {
    }

    static void init() {
        new MinecraftCursorInitializerImpl().init(CursorManager.INSTANCE, CursorTypeResolver.INSTANCE);

        Services.PLATFORM.getEntrypoints().forEach(entrypoint -> {
            try {
                entrypoint.init(CursorManager.INSTANCE, CursorTypeResolver.INSTANCE);
            } catch (LinkageError | Exception e) {
                LOGGER.error("[minecraft-cursor] Skipping invalid implementation of MinecraftCursorInitializer", e);
            }
        });

        CursorControllerProvider.init(CONTROLLER);
    }

    /**
     * Execute before any screen render.
     */
    static void afterClientTick(Minecraft minecraft) {
        if (!ExternalCursorTracker.get().isCustom()) {
            if (minecraft.screen == null && deferredCursorType == null) {
                CursorManager.INSTANCE.setCurrentCursor(CursorType.firstNonDefault(
                        ExternalCursorTracker.get().getCursorOrDefault(),
                        CONTROLLER.consumeTransientCursor(),
                        CONTROLLER.consumeTransientFallbackCursor()
                ));
            } else if (deferredCursorType != null && shouldApplyDeferredCursorType(minecraft)) {
                CursorManager.INSTANCE.setCurrentCursor(deferredCursorType);
            }
            deferredCursorType = null;
        }
    }

    /**
     * Execute after {@link Screen#render(GuiGraphics, int, int, float)} and before {@link Screen#renderWithTooltip(GuiGraphics, int, int, float)} ends.
     */
    public static void afterScreenRender(Minecraft minecraft, Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (shouldApplyDeferredCursorType(minecraft)) {
            CursorTypeResolver.INSTANCE.getInspector().render(minecraft, screen, guiGraphics, mouseX, mouseY);
            if (!ExternalCursorTracker.get().isCustom()) {
                deferredCursorType = resolveWithFallback(screen, mouseX, mouseY);
            }
        }
    }

    /**
     * Execute after {@link Screen#renderWithTooltip(GuiGraphics, int, int, float)}.
     */
    static void afterCurrentScreenRender(Minecraft minecraft, Screen currentScreen, GuiGraphics guiGraphics, int mouseX, int mouseY) {
        CursorTypeResolver.INSTANCE.getInspector().render(minecraft, currentScreen, guiGraphics, mouseX, mouseY);

        if (!ExternalCursorTracker.get().isCustom()) {
            CursorManager.INSTANCE.setCurrentCursor(resolveWithFallback(currentScreen, mouseX, mouseY));
        }

        deferredCursorType = null;
    }

    private static boolean shouldApplyDeferredCursorType(Minecraft minecraft) {
        return minecraft.screen == null && !minecraft.mouseHandler.isMouseGrabbed();
    }

    private static @NotNull CursorType resolveWithFallback(Screen screen, double mouseX, double mouseY) {
        return CursorType.firstNonDefault(resolveCursorType(screen, mouseX, mouseY), CONTROLLER.consumeTransientFallbackCursor());
    }

    private static CursorType resolveCursorType(Screen screen, double mouseX, double mouseY) {
        if (!CursorManager.INSTANCE.isAdaptive()) {
            return CursorType.DEFAULT;
        }

        CursorType cursorType = CursorType.firstNonDefault(ExternalCursorTracker.get().getCursorOrDefault(), CONTROLLER.consumeTransientCursor());
        if (!cursorType.isDefault()) {
            return cursorType;
        }

        if (CursorTypeUtil.isGrabbing()) {
            return CursorType.GRABBING;
        }

        CursorType resolved = CursorTypeResolver.INSTANCE.resolve(screen, mouseX, mouseY);
        if (!resolved.isDefault()) {
            return resolved;
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
