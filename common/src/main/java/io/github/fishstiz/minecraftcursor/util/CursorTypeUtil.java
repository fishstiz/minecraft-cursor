package io.github.fishstiz.minecraftcursor.util;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class CursorTypeUtil {
    private CursorTypeUtil() {
    }

    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static boolean canShift() {
        long handle = CLIENT.getWindow().getWindow();
        return CursorManager.INSTANCE.getCursor(CursorType.SHIFT).getId() != 0
                && (InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_SHIFT));
    }

    public static boolean isGrabbing() {
        return CursorManager.INSTANCE.getCursor(CursorType.GRABBING).getId() != 0
                && CursorManager.INSTANCE.getCurrentCursor().getType() == CursorType.GRABBING
                && isLeftClickHeld();
    }

    public static boolean isLeftClickHeld() {
        return GLFW.glfwGetMouseButton(CLIENT.getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;
    }
}
