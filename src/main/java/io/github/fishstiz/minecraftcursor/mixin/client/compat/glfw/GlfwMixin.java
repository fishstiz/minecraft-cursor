package io.github.fishstiz.minecraftcursor.mixin.client.compat.glfw;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

@Mixin(value = GLFW.class, remap = false)
public class GlfwMixin {
    @Unique
    private static final MinecraftClient MINECRAFT = MinecraftClient.getInstance();

    @Unique
    private static final HashMap<Long, CursorType> standardCursors = new HashMap<>();

    @Unique
    private static boolean isMinecraftCursor = false;

    @Unique
    private static boolean hasStandardCursors = false;

    @Unique
    private static void setExternalCursor(CursorType externalCursor) {
        MinecraftCursor.getInstance().setExternalCursor(externalCursor);
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
            standardCursors.put(id, cursorType);
            hasStandardCursors = true;
        }

        return id;
    }

    @WrapMethod(method = "glfwDestroyCursor")
    private static void removeStandardCursor(long cursor, Operation<Void> original) {
        original.call(cursor);
        standardCursors.remove(cursor);
    }

    @WrapMethod(method = "glfwSetCursor")
    private static void setMinecraftCursor(long window, long cursor, Operation<Void> original) {
        if (!hasStandardCursors || window != MINECRAFT.getWindow().getHandle()) {
            original.call(window, cursor);
            return;
        }

        if (CursorManager.INSTANCE.isMinecraftCursor(cursor)) {
            original.call(window, cursor);
            isMinecraftCursor = true;
            return;
        }

        CursorType minecraftCursor = standardCursors.get(cursor);

        if (minecraftCursor == null || CursorManager.INSTANCE.getCursor(minecraftCursor).getId() == 0) {
            original.call(window, cursor);
            setExternalCursor(CursorType.DEFAULT);
            isMinecraftCursor = false;
        } else {
            setExternalCursor(minecraftCursor);
            if (!isMinecraftCursor) CursorManager.INSTANCE.reloadCursor();
        }
    }
}
