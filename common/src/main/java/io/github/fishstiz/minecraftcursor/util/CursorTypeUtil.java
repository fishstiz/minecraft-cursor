package io.github.fishstiz.minecraftcursor.util;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.Minecraft;
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
}
