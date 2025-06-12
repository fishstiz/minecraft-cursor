package io.github.fishstiz.minecraftcursor.mixin;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.util.SettingsUtil;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "resizeDisplay", at = @At("TAIL"))
    public void reloadCursorsOnResize(CallbackInfo ci) {
        CursorManager.INSTANCE.getCursors().forEach(cursor -> {
            if (cursor.isEnabled() && SettingsUtil.isAutoScale(cursor.getScale())) {
                MinecraftCursor.LOGGER.info("[minecraft-cursor] Reloading auto-scaled cursor on resize: '{}'", cursor.getTypeKey());
                cursor.reload();
            }
        });
    }
}
