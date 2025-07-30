package io.github.fishstiz.testmod.compat.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class MinecraftCursorUtil {
    private MinecraftCursorUtil() {
    }

    public static MutableComponent getTranslation(String key) {
        return Component.translatable("minecraft-cursor.options.cursor-type." + key);
    }

    public static MutableComponent getTranslation(CursorType cursorType) {
        return getTranslation(cursorType.getKey());
    }
}
