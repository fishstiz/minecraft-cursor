package io.github.fishstiz.minecraftcursor.util;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class CursorTypeUtil {
    private CursorTypeUtil() {
    }

    public static final long WINDOW = Minecraft.getInstance().getWindow().getWindow();

    public static boolean canShift() {
        return CursorManager.INSTANCE.isEnabled(CursorType.SHIFT) &&
               (InputConstants.isKeyDown(WINDOW, GLFW.GLFW_KEY_LEFT_SHIFT) ||
                InputConstants.isKeyDown(WINDOW, GLFW.GLFW_KEY_RIGHT_SHIFT));
    }

    public static boolean isGrabbing() {
        return CursorManager.INSTANCE.isEnabled(CursorType.GRABBING) &&
               CursorManager.INSTANCE.getAppliedCursor().getType().isKey(CursorType.GRABBING) &&
               isLeftClickHeld();
    }

    public static boolean isLeftClickHeld() {
        return GLFW.glfwGetMouseButton(WINDOW, GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;
    }

    public static @Nullable CursorType mapGLFWCursor(int shape) {
        return switch (shape) {
            case GLFW.GLFW_ARROW_CURSOR -> CursorType.DEFAULT;
            case GLFW.GLFW_POINTING_HAND_CURSOR -> CursorType.POINTER;
            case GLFW.GLFW_IBEAM_CURSOR -> CursorType.TEXT;
            case GLFW.GLFW_CROSSHAIR_CURSOR -> CursorType.CROSSHAIR;
            case GLFW.GLFW_RESIZE_EW_CURSOR -> CursorType.RESIZE_EW;
            case GLFW.GLFW_RESIZE_NS_CURSOR -> CursorType.RESIZE_NS;
            case GLFW.GLFW_RESIZE_NWSE_CURSOR -> CursorType.RESIZE_NWSE;
            case GLFW.GLFW_RESIZE_NESW_CURSOR -> CursorType.RESIZE_NESW;
            case GLFW.GLFW_RESIZE_ALL_CURSOR -> CursorType.GRABBING;
            case GLFW.GLFW_NOT_ALLOWED_CURSOR -> CursorType.NOT_ALLOWED;
            default -> null;
        };
    }
}
