package io.github.fishstiz.minecraftcursor.mixin.compat.veil;

import foundry.veil.impl.client.imgui.VeilImGuiImplGlfw;
import imgui.ImGui;
import imgui.glfw.ImGuiImplGlfw;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import org.spongepowered.asm.mixin.Mixin;

import static imgui.flag.ImGuiConfigFlags.NoMouseCursorChange;

@Mixin(VeilImGuiImplGlfw.class)
public class VeilImGuiImplGlfwMixin extends ImGuiImplGlfw {
    @Override
    public boolean init(long window, boolean installCallbacks) {
        if (ImGui.getCurrentContext().isValidPtr() && MinecraftCursor.CONFIG.isVeilCursorChangesDisabled()) {
            ImGui.getIO().addConfigFlags(NoMouseCursorChange);
        }

        return super.init(window, installCallbacks);
    }
}
