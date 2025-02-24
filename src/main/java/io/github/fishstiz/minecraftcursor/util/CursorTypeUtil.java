package io.github.fishstiz.minecraftcursor.util;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CursorTypeUtil {
    private CursorTypeUtil() {
    }

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static boolean canShift() {
        long handle = CLIENT.getWindow().getHandle();
        return CursorManager.INSTANCE.getCursor(CursorType.SHIFT).getId() != 0
                && (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT));
    }

    public static boolean isGrabbing() {
        return CursorManager.INSTANCE.getCursor(CursorType.GRABBING).getId() != 0
                && CursorManager.INSTANCE.getCurrentCursor().getType() == CursorType.GRABBING
                && isLeftClickHeld();
    }

    public static boolean isLeftClickHeld() {
        return GLFW.glfwGetMouseButton(CLIENT.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;
    }
}
