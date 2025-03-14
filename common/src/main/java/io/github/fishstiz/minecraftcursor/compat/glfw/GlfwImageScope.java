package io.github.fishstiz.minecraftcursor.compat.glfw;

public interface GlfwImageScope {
    void minecraft_cursor$setInternal(boolean internal);

    default boolean minecraft_cursor$isInternal() {
        return false;
    }
}
