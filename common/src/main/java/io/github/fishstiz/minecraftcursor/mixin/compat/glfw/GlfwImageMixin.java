package io.github.fishstiz.minecraftcursor.mixin.compat.glfw;

import io.github.fishstiz.minecraftcursor.compat.glfw.GlfwImageScope;
import org.lwjgl.glfw.GLFWImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GLFWImage.class)
public class GlfwImageMixin implements GlfwImageScope {
    @Unique
    private boolean minecraft_cursor$internal = false;

    @Override
    public void minecraft_cursor$setInternal(boolean internal) {
        this.minecraft_cursor$internal = internal;
    }

    @Override
    public boolean minecraft_cursor$isInternal() {
        return minecraft_cursor$internal;
    }
}
