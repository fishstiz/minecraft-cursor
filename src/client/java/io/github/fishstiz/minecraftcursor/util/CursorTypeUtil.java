package io.github.fishstiz.minecraftcursor.util;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static io.github.fishstiz.minecraftcursor.MinecraftCursorClient.CLIENT;
import static io.github.fishstiz.minecraftcursor.MinecraftCursorClient.CURSOR_MANAGER;

public class CursorTypeUtil {
    public static boolean canShift() {
        long handle = CLIENT.getWindow().getHandle();
        return CURSOR_MANAGER.getCursor(CursorType.SHIFT).getId() != 0
                && (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT)
                || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT));
    }

    public static boolean isGrabbing() {
        return CURSOR_MANAGER.getCursor(CursorType.GRABBING).getId() != 0
                && CURSOR_MANAGER.getCurrentCursor().getType() == CursorType.GRABBING
                && isLeftClickHeld();
    }

    public static boolean isLeftClickHeld() {
        return GLFW.glfwGetMouseButton(CLIENT.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;
    }
}
