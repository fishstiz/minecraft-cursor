package io.github.fishstiz.minecraftcursor.registry.utils;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CursorTypeUtils {
    public static boolean canShift() {
        long handle = MinecraftCursorClient.CLIENT.getWindow().getHandle();
        return MinecraftCursorClient.CURSOR_MANAGER.getCursor(CursorType.SHIFT).getId() != 0
                && (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT)
                || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT));
    }
}
