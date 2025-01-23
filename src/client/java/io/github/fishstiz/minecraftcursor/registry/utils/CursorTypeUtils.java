package io.github.fishstiz.minecraftcursor.registry.utils;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CursorTypeUtils {
    public static boolean canShift(boolean isGrabbing) {
        long handle = MinecraftCursorClient.CLIENT.getWindow().getHandle();
        CursorType cursorType = isGrabbing ? CursorType.SHIFT_GRABBING : CursorType.SHIFT;
        return MinecraftCursorClient.CURSOR_MANAGER.getCursor(cursorType).getId() != 0
                && (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT)
                || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT));
    }
}
