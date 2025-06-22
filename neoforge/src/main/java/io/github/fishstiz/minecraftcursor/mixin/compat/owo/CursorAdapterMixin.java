package io.github.fishstiz.minecraftcursor.mixin.compat.owo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.compat.ExternalCursor;
import io.github.fishstiz.minecraftcursor.compat.ExternalCursorTracker;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import io.wispforest.owo.ui.util.CursorAdapter;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;
import java.util.function.Consumer;

@Mixin(value = CursorAdapter.class, remap = false)
public abstract class CursorAdapterMixin {
    @Unique
    private static final int minecraft_cursor$OWO = CursorAdapter.class.hashCode();

    @WrapOperation(method = "<init>", at = @At(
            value = "INVOKE",
            target = "Lorg/lwjgl/glfw/GLFW;glfwCreateStandardCursor(I)J"
    ))
    private long trackStandardCursors(int shape, Operation<Long> original) {
        long cursor = original.call(shape);

        if (cursor != MemoryUtil.NULL) {
            CursorType cursorType = CursorTypeUtil.mapGLFWCursor(shape);
            if (cursorType != null) {
                ExternalCursorTracker.trackCursor(cursor, minecraft_cursor$OWO, cursorType);
            }
        }

        return cursor;
    }

    @WrapOperation(method = "dispose", at = @At(
            value = "INVOKE",
            target = "Ljava/util/Collection;forEach(Ljava/util/function/Consumer;)V"
    ))
    private void untrackStandardCursors(Collection<Long> instance, Consumer<Long> consumer, Operation<Void> original) {
        instance.forEach(ExternalCursorTracker.get()::untrackCursor);
        original.call(instance, consumer);
    }

    @WrapOperation(method = "applyStyle", at = @At(
            value = "INVOKE",
            target = "Lorg/lwjgl/glfw/GLFW;glfwSetCursor(JJ)V"
    ))
    private void updateCursor(long window, long cursor, Operation<Void> original) {
        boolean nullCursor = cursor == MemoryUtil.NULL;
        ExternalCursor trackedCursor = ExternalCursorTracker.get().getTrackedCursor(cursor);

        if (window != CursorTypeUtil.WINDOW || (!nullCursor && trackedCursor == null)) {
            original.call(window, cursor);
            return;
        }

        ExternalCursorTracker.get().updateCursor(minecraft_cursor$OWO, nullCursor ? CursorType.DEFAULT : trackedCursor.getCursorType());
    }
}
