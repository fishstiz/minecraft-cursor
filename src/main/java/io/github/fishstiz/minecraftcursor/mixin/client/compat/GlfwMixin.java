package io.github.fishstiz.minecraftcursor.mixin.client.compat;

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
    private static final HashMap<Long, Integer> standardCursors = new HashMap<>();

    @Unique
    private static boolean isMinecraftCursor = false;

    @Unique
    private static void setExternalCursor(CursorType externalCursor) {
        MinecraftCursor.getInstance().setExternalCursor(externalCursor);
    }

    @WrapMethod(method = "glfwCreateStandardCursor")
    private static long mapStandardCursor(int shape, Operation<Long> original) {
        long id = original.call(shape);
        standardCursors.put(id, shape);
        return id;
    }

    @WrapMethod(method = "glfwDestroyCursor")
    private static void removeStandardCursor(long cursor, Operation<Void> original) {
        original.call(cursor);
        standardCursors.remove(cursor);
    }

    @WrapMethod(method = "glfwSetCursor")
    private static void convertToMinecraftCursor(long window, long cursor, Operation<Void> original) {
        if (window != MINECRAFT.getWindow().getHandle()) {
            original.call(window, cursor);
            return;
        }

        if (CursorManager.INSTANCE.isMinecraftCursor(cursor)) {
            original.call(window, cursor);
            isMinecraftCursor = true;
            return;
        }

        CursorType minecraftCursor = switch (standardCursors.getOrDefault(cursor, -1)) {
            case GLFW_ARROW_CURSOR -> CursorType.DEFAULT;
            case GLFW_POINTING_HAND_CURSOR -> CursorType.POINTER;
            case GLFW_IBEAM_CURSOR -> CursorType.TEXT;
            default -> null;
        };

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
