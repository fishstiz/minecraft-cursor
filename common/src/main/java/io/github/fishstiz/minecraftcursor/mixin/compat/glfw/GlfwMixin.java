package io.github.fishstiz.minecraftcursor.mixin.compat.glfw;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.compat.ExternalCursor;
import io.github.fishstiz.minecraftcursor.compat.ExternalCursorTracker;
import io.github.fishstiz.minecraftcursor.compat.CursorTracker;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.stream.Stream;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;
import static io.github.fishstiz.minecraftcursor.MinecraftCursor.LOGGER;
import static io.github.fishstiz.minecraftcursor.compat.ExternalCursorTracker.*;

import static org.lwjgl.glfw.GLFW.*;

// only works on Fabric, NeoForge doesn't allow mixin of library
@Mixin(value = GLFW.class, remap = false)
public abstract class GlfwMixin {
    @Unique
    private static String minecraft_cursor$getCaller(Stream<StackWalker.StackFrame> frames) {
        return frames.skip(2)
                .dropWhile(frame -> frame.getDeclaringClass() == GLFW.class)
                .findFirst()
                .map(frame -> frame.getDeclaringClass().getPackageName())
                .orElse("placeholder");
    }

    @WrapMethod(method = "nglfwCreateCursor")
    private static long ntrackCustomCursor(long image, int xhot, int yhot, Operation<Long> original) {
        if (ExternalCursorTracker.get().consumeAddress(image)) {
            return original.call(image, xhot, yhot);
        }

        long id = original.call(image, xhot, yhot);
        String caller = getWalker().walk(GlfwMixin::minecraft_cursor$getCaller);
        trackCursor(id, caller.hashCode());

        LOGGER.warn("[minecraft-cursor] Detected custom cursor from '{}'. Expect compatibility issues.", caller);
        return id;
    }

    @WrapMethod(method = "glfwCreateStandardCursor")
    private static long mapStandardCursor(int shape, Operation<Long> original) {
        long id = original.call(shape);

        CursorType cursorType = switch (shape) {
            case GLFW_ARROW_CURSOR -> CursorType.DEFAULT;
            case GLFW_POINTING_HAND_CURSOR -> CursorType.POINTER;
            case GLFW_IBEAM_CURSOR -> CursorType.TEXT;
            case GLFW_CROSSHAIR_CURSOR -> CursorType.CROSSHAIR;
            case GLFW_RESIZE_EW_CURSOR -> CursorType.RESIZE_EW;
            case GLFW_RESIZE_NS_CURSOR -> CursorType.RESIZE_NS;
            case GLFW_RESIZE_NWSE_CURSOR -> CursorType.RESIZE_NWSE;
            case GLFW_RESIZE_NESW_CURSOR -> CursorType.RESIZE_NESW;
            case GLFW_RESIZE_ALL_CURSOR -> CursorType.GRABBING;
            case GLFW_NOT_ALLOWED_CURSOR -> CursorType.NOT_ALLOWED;
            default -> null;
        };

        if (cursorType != null) {
            ExternalCursor externalCursor = ExternalCursorTracker.get().getTrackedCursor(id);
            if (externalCursor == null) {
                String caller = getWalker().walk(GlfwMixin::minecraft_cursor$getCaller);
                LOGGER.info("[minecraft-cursor] Remapping cursor to '{}' from '{}'", cursorType.getKey(), caller);
                trackCursor(id, caller.hashCode(), cursorType);
            } else {
                externalCursor.update(cursorType);
            }
        }

        return id;
    }

    @WrapMethod(method = "glfwDestroyCursor")
    private static void removeCursor(long cursor, Operation<Void> original) {
        original.call(cursor);
        ExternalCursorTracker.get().untrackCursor(cursor);
    }

    @WrapMethod(method = "glfwSetCursor")
    private static void setMinecraftCursor(long window, long cursor, Operation<Void> original) {
        if (!isTracking() || window != Minecraft.getInstance().getWindow().getWindow()) {
            original.call(window, cursor);
            return;
        }

        CursorTracker tracker = ExternalCursorTracker.get();
        if (!CONFIG.isRemapCursorsEnabled() || !tracker.isTracking(cursor)) {
            original.call(window, cursor);
            return;
        }

        ExternalCursor externalCursor = tracker.getTrackedCursor(cursor);
        if (externalCursor == null
                || externalCursor.getCursorType() == ExternalCursor.PLACEHOLDER_CUSTOM
                || CursorManager.INSTANCE.getCursor(externalCursor.getCursorType()).getId() == 0) {
            original.call(window, cursor);
            tracker.updateCursor(externalCursor == null ? 0 : externalCursor.getCaller(), ExternalCursor.PLACEHOLDER_CUSTOM);
        } else {
            tracker.updateCursor(externalCursor.getCaller(), externalCursor.getCursorType());
            if (!tracker.isCustom()) original.call(window, CursorManager.INSTANCE.getCurrentCursor().getId());
        }
    }
}
