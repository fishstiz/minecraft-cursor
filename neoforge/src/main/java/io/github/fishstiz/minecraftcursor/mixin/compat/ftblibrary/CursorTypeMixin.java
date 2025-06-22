package io.github.fishstiz.minecraftcursor.mixin.compat.ftblibrary;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.compat.CursorTracker;
import io.github.fishstiz.minecraftcursor.compat.ExternalCursor;
import io.github.fishstiz.minecraftcursor.compat.ExternalCursorTracker;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = dev.ftb.mods.ftblibrary.ui.CursorType.class, remap = false)
public class CursorTypeMixin {
    @Unique
    private static final int minecraft_cursor$FTB_LIBRARY = dev.ftb.mods.ftblibrary.ui.CursorType.class.hashCode();

    @Inject(
            method = "set",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwCreateStandardCursor(I)J")),
            at = @At(value = "FIELD:FIRST", target = "Ldev/ftb/mods/ftblibrary/ui/CursorType;cursor:J", shift = At.Shift.AFTER, ordinal = 0)
    )
    private static void minecraft_cursor$trackCursor(dev.ftb.mods.ftblibrary.ui.CursorType type, CallbackInfo ci) {
        if (type != null) {
            CursorTypeAccess accessor = (CursorTypeAccess) (Object) type;
            long cursor = accessor.minecraft_cursor$getCursor();
            CursorType cursorType = CursorTypeUtil.mapGLFWCursor(accessor.minecraft_cursor$getShape());
            if (cursorType != null) {
                ExternalCursorTracker.trackCursor(cursor, minecraft_cursor$FTB_LIBRARY, cursorType);
            }
        }
    }

    @Inject(method = "set", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSetCursor(JJ)V"), cancellable = true)
    private static void minecraft_cursor$updateCursor(dev.ftb.mods.ftblibrary.ui.CursorType type, CallbackInfo ci) {
        if (MinecraftCursor.CONFIG.isRemapCursorsEnabled()) {
            CursorTracker tracker = ExternalCursorTracker.get();
            CursorType cursorType = CursorType.DEFAULT;

            if (type != null) {
                ExternalCursor externalCursor = tracker.getTrackedCursor(((CursorTypeAccess) (Object) type).minecraft_cursor$getCursor());
                if (externalCursor != null) {
                    cursorType = externalCursor.getCursorType();
                }
            }

            tracker.updateCursor(minecraft_cursor$FTB_LIBRARY, cursorType);
            ci.cancel();
        }
    }
}
