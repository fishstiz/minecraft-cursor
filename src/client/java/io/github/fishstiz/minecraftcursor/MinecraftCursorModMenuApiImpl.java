package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.gui.MinecraftCursorOptionsScreen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class MinecraftCursorModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> new MinecraftCursorOptionsScreen(screen, MinecraftCursorClient.getConfigManager(), MinecraftCursorClient.getCursorHandler());
    }
}
