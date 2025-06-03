package io.github.fishstiz.minecraftcursor.mixin.compat.glfw;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.compat.CursorTracker;
import io.github.fishstiz.minecraftcursor.compat.ExternalCursor;
import io.github.fishstiz.minecraftcursor.compat.ExternalCursorTracker;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;


import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;
import static io.github.fishstiz.minecraftcursor.MinecraftCursor.LOGGER;
import static io.github.fishstiz.minecraftcursor.compat.ExternalCursorTracker.*;

import static org.lwjgl.glfw.GLFW.*;

// only works on Fabric, NeoForge doesn't allow mixin of library
@Mixin(value = GLFW.class, remap = false)
public abstract class GLFWMixin {
    @WrapMethod(method = "nglfwCreateCursor")
    private static long trackCustomCursor(long image, int xhot, int yhot, Operation<Long> original) {
        if (ExternalCursorTracker.consumeInternalCursor(image)) {
            return original.call(image, xhot, yhot);
        }

        String packageName = getWalker().walk(ExternalCursorTracker::getCallerPackage);
        long id = original.call(image, xhot, yhot);

        if (isInternalPackage(packageName)) {
            return id;
        }

        trackCursor(id, packageName.hashCode());

        LOGGER.warn("[minecraft-cursor] Detected custom cursor from '{}'. Expect compatibility issues.", packageName);
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
                String packageName = getWalker().walk(ExternalCursorTracker::getCallerPackage);
                LOGGER.info("[minecraft-cursor] Remapping cursor to '{}' from '{}'", cursorType.getKey(), packageName);
                trackCursor(id, packageName.hashCode(), cursorType);
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
        if (!isTracking() || window != CursorTypeUtil.WINDOW || !CONFIG.isRemapCursorsEnabled()) {
            original.call(window, cursor);
            return;
        }

        CursorTracker tracker = ExternalCursorTracker.get();
        if (!tracker.isTracking(cursor) && cursor != 0) {
            original.call(window, cursor);
            return;
        }

        if (cursor == 0) {
            String packageName = getWalker().walk(ExternalCursorTracker::getCallerPackage);
            if (isInternalPackage(packageName)) {
                original.call(window, cursor);
            } else {
                tracker.updateCursor(packageName.hashCode(), CursorType.DEFAULT);
                if (!tracker.isCustom()) {
                    original.call(window, CursorManager.INSTANCE.getCurrentId());
                }
            }
            return;
        }

        ExternalCursor trackedCursor = tracker.getTrackedCursor(cursor);

        if (trackedCursor == null
            || trackedCursor.getCursorType().isKey(ExternalCursor.CUSTOM)
            || !CursorManager.INSTANCE.isEnabled(trackedCursor.getCursorType())) {
            original.call(window, cursor);
            tracker.updateCursor(trackedCursor == null ? 0 : trackedCursor.getCaller(), ExternalCursor.CUSTOM);
            return;
        }

        tracker.updateCursor(trackedCursor.getCaller(), trackedCursor.getCursorType());

        if (!tracker.isCustom()) {
            original.call(window, CursorManager.INSTANCE.getCurrentId());
        }
    }
}
