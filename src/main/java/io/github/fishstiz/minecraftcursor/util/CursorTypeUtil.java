package io.github.fishstiz.minecraftcursor.util;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CursorTypeUtil {
    private CursorTypeUtil() {
    }

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static boolean canShift() {
        long handle = client.getWindow().getHandle();
        return getCursorManager().getCursor(CursorType.SHIFT).getId() != 0
                && (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT));
    }

    public static boolean isGrabbing() {
        return getCursorManager().getCursor(CursorType.GRABBING).getId() != 0
                && getCursorManager().getCurrentCursor().getType() == CursorType.GRABBING
                && isLeftClickHeld();
    }

    public static boolean isLeftClickHeld() {
        return GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;
    }

    private static class CursorManagerHolder {
        private static final CursorManager INSTANCE = io.github.fishstiz.minecraftcursor.MinecraftCursor.getCursorManager();
    }

    private static CursorManager getCursorManager() {
        return CursorManagerHolder.INSTANCE;
    }
}
