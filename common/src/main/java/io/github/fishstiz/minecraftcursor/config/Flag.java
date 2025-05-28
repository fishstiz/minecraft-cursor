package io.github.fishstiz.minecraftcursor.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Flag {
    REMAP("minecraft-cursor.options.compat.remap_cursors.info", "minecraft-cursor.options.compat.remap_cursors.info_unavailable");

    private final String infoKey;
    private final String disabledInfoKey;
    private boolean enabled = true;

    Flag(@NotNull String infoKey, @Nullable String disabledInfoKey) {
        this.infoKey = infoKey;
        this.disabledInfoKey = disabledInfoKey != null ? disabledInfoKey : infoKey;
    }

    public void disable() {
        this.enabled = false;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public @NotNull String getInfoKey() {
        return this.isEnabled() ? this.infoKey : this.disabledInfoKey;
    }
}
